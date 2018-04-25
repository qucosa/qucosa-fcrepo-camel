package de.qucosa.oaiprovider.component;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultProducer;

public class UpdateCacheEndpoint extends DefaultEndpoint {
    private Endpoint endpoint;
    
    public UpdateCacheEndpoint(String endpointUri, Component component, OaiProviderConfiguration configuration) {
        super(endpointUri, component);
        endpoint = this;
    }

    @Override
    public Consumer createConsumer(Processor processor) {
        return null;
    }

    @Override
    public Producer createProducer() {
        return new DefaultProducer(endpoint) {
            
            @Override
            public void process(Exchange exchange) {
                System.out.println(exchange.getIn().getBody());
            }
        };
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
