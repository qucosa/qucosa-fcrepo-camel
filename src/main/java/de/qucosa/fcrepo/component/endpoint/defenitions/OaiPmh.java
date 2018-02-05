package de.qucosa.fcrepo.component.endpoint.defenitions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultProducer;

import de.qucosa.fcrepo.component.EndpointDefAbstract;
import de.qucosa.fcrepo.component.EndpointDefAnnotation;
import de.qucosa.fcrepo.component.EndpointDefInterface;
import de.qucosa.fcrepo.fedora.api.FedoraClient;
import de.qucosa.fcrepo.fedora.api.mappings.xml.Identifier;
import de.qucosa.fcrepo.fedora.api.services.FedoraOaiService;
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceFactory;
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceInterface;
import de.qucosa.fcrepo.fedora.api.services.PersistenceService;

@EndpointDefAnnotation(isConsumer = true, isProducer = true)
public class OaiPmh<T> extends EndpointDefAbstract implements EndpointDefInterface {
    @Override
    public Consumer getConsumer() {
        return new DefaultConsumer(endpoint, processor) {
            private FedoraClient fedoraClient;
            
            @Override
            protected void doStart() throws Exception {
                super.doStart();
                fedoraClient = new FedoraClient(endpoint.getUser(), endpoint.getPassword());
                fedoraClient.setShema(endpoint.getShema());
                fedoraClient.setHost(endpoint.getHost());
                fedoraClient.setPort(endpoint.getPort());
                Map<Object, T> params = new HashMap<>();
                params.put("token", null);
                FedoraServiceInterface service = FedoraServiceFactory.createService(FedoraOaiService.class);
                service.setFedoraClient(fedoraClient);
                service.run(service, "filledIdientifiers", params);
                Exchange exchange = endpoint.createExchange();
                exchange.getIn().setBody(service.getServiceDataObject());
                processor.process(exchange);
            }
        };
    }
    
    @Override
    public Producer getProducer() {
        return new DefaultProducer(endpoint) {
            
            @Override
            public void process(Exchange exchange) throws Exception {
                List<Identifier> identifiers = (List<Identifier>) exchange.getIn().getBody();
                FedoraServiceInterface service = FedoraServiceFactory.createService(PersistenceService.class);
                service.setServiceDataObject(identifiers);
                service.run(service, "saveIdentifiers", null);
            }
        };
    }
}
