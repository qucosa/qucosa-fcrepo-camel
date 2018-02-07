package de.qucosa.fcrepo.component.endpoint.defenitions;

import java.sql.ResultSet;
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
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceInterface;
import de.qucosa.fcrepo.fedora.api.services.PersistenceService;

@EndpointDefAnnotation(isConsumer = true, isProducer = true)
public class LoadDatastream<T> extends EndpointDefAbstract implements EndpointDefInterface {
    @Override
    public Consumer getConsumer() {
        return new DefaultConsumer(endpoint, processor) {
            
            @SuppressWarnings("unchecked")
            @Override
            protected void doStart() throws Exception {
                super.doStart();
                Map<Object, T> params = new HashMap<>();
                params.put("stmt", (T) "SELECT id, identifier, SUBSTRING(identifier, 'qucosa:\\d+$') AS pid FROM identifier WHERE identifier ~ 'qucosa:\\d+$';");
                FedoraServiceInterface service = FedoraServiceFactory.createService(PersistenceService.class);
                service.run(service, "getIdentifieres", params);
                ResultSet data = service.getServiceDataObject();
                Exchange exchange = endpoint.createExchange();
                exchange.setProperty("fedora", endpoint);
                exchange.getIn().setBody(data);
                processor.process(exchange);
            }
        };
    }

    @Override
    public Producer getProducer() {
        return new DefaultProducer(endpoint) {
            
            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println("DataSreamProducer: " + exchange.getIn().getBody());
            }
        };
    }

}
