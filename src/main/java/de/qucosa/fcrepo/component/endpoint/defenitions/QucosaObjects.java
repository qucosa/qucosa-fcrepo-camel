package de.qucosa.fcrepo.component.endpoint.defenitions;

import java.util.Set;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultProducer;

import de.qucosa.fcrepo.component.EndpointDefAbstract;
import de.qucosa.fcrepo.component.EndpointDefInterface;
import de.qucosa.fcrepo.fedora.api.FedoraClient;
import de.qucosa.fcrepo.fedora.api.pojos.Format;
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceFactory;
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceInstanceException;
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceInterface;
import de.qucosa.fcrepo.fedora.api.services.PersistenceService;
import de.qucosa.fcrepo.fedora.api.services.QucosaService;

public class QucosaObjects extends EndpointDefAbstract implements EndpointDefInterface {

    @Override
    public Consumer getConsumer() {
        return new DefaultConsumer(endpoint, processor) {
            private FedoraClient fedoraClient;
            
            private FedoraServiceInterface service;
            
            @Override
            protected void doStart() throws Exception {
                super.doStart();
                init();
                service.run(service, "getQucosaObjectDefinitions", null);
                Exchange exchange = endpoint.createExchange();
                exchange.setProperty("fedora", endpoint);
                exchange.getIn().setBody(service.getServiceDataObject());
                processor.process(exchange);
            }
            
            private void init() {
                fedoraClient = new FedoraClient(endpoint.getUser(), endpoint.getPassword());
                fedoraClient.setShema(endpoint.getShema());
                fedoraClient.setHost(endpoint.getHost());
                fedoraClient.setPort(endpoint.getPort());
                try {
                    service = FedoraServiceFactory.createService(QucosaService.class);
                    service.setFedoraClient(fedoraClient);
                } catch (FedoraServiceInstanceException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public Producer getProducer() {
        return new DefaultProducer(endpoint) {
            
            @SuppressWarnings("unchecked")
            @Override
            public void process(Exchange exchange) throws Exception {
                Set<Format> formats = (Set<Format>) exchange.getIn().getBody();
                FedoraServiceInterface service = FedoraServiceFactory.createService(PersistenceService.class);
                service.setServiceDataObject(formats);
                service.run(service, "saveFormats", null);
            }
        };
    }

}
