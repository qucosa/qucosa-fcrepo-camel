package de.qucosa.fcrepo.component.endpoint.defenitions;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultProducer;

import de.qucosa.fcrepo.component.EndpointDefAbstract;
import de.qucosa.fcrepo.component.EndpointDefInterface;

public class ReportingDb extends EndpointDefAbstract implements EndpointDefInterface {

    @Override
    public Consumer getConsumer() {
        return null;
    }

    @Override
    public Producer getProducer() {
        return new DefaultProducer(endpoint) {
            
            @Override
            public void process(Exchange exchange) throws Exception {
//                System.out.println("ReportingDB: " + exchange.getIn().getBody());
            }
        };
    }

}
