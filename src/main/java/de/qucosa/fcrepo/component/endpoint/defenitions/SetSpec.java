package de.qucosa.fcrepo.component.endpoint.defenitions;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultProducer;

import de.qucosa.fcrepo.component.EndpointDefAbstract;
import de.qucosa.fcrepo.component.EndpointDefAnnotation;
import de.qucosa.fcrepo.component.EndpointDefInterface;
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceFactory;
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceInstanceException;
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceInterface;
import de.qucosa.fcrepo.fedora.api.services.PersistenceService;
import de.qucosa.fcrepo.fedora.api.services.SetSpecService;

@EndpointDefAnnotation(isConsumer = true, isProducer = true)
public class SetSpec<T> extends EndpointDefAbstract implements EndpointDefInterface {

    @Override
    public Consumer getConsumer() {
        
        try {
            return new DefaultConsumer(endpoint, processor) {
                FedoraServiceInterface setSpecService = FedoraServiceFactory.createService(SetSpecService.class);
                
                @Override
                protected void doStart() throws Exception {
                    super.doStart();
                    Map<Object, T> params = new HashMap<>();
                    setSpecService.run(setSpecService, "", params);
                    Exchange exchange = endpoint.createExchange();
                    exchange.getIn().setBody(setSpecService.getServiceDataObject());
                    processor.process(exchange);
                }
                
                @Override
                protected void doStop() throws Exception {
                    super.doStop();
                }
            };
        } catch (FedoraServiceInstanceException e) {
            return null;
        }
    }

    @Override
    public Producer getProducer() {
        try {
            return new DefaultProducer(endpoint) {
                FedoraServiceInterface service = FedoraServiceFactory.createService(PersistenceService.class);
                
                @Override
                public void process(Exchange exchange) throws Exception {
                    Map<Object, T> params = new HashMap<>();
                    service.setServiceDataObject(exchange.getIn().getBody());
                    service.run(service, "saveSetSpecs", params);
                }
                
                @Override
                protected void doStop() throws Exception {
                    super.doStop();
                }
            };
        } catch (FedoraServiceInstanceException ignore) {
            return null;
        }
    }

}
