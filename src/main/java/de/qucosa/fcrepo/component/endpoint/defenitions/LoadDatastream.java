package de.qucosa.fcrepo.component.endpoint.defenitions;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultProducer;

import de.qucosa.fcrepo.component.EndpointDefAbstract;
import de.qucosa.fcrepo.component.EndpointDefAnnotation;
import de.qucosa.fcrepo.component.EndpointDefInterface;

@EndpointDefAnnotation(isConsumer = true, isProducer = true)
public class LoadDatastream extends EndpointDefAbstract implements EndpointDefInterface {
    @Override
    public Consumer getConsumer() {
        return new DefaultConsumer(endpoint, processor) {
            
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
