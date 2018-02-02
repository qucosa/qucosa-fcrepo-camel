package de.qucosa.fcrepo.component;

import org.apache.camel.Processor;

public abstract class EndpointDefAbstract implements EndpointDefInterface {
    protected FedoraEndpoint endpoint;
    
    protected Processor processor;
    
    @Override
    public void setEndpoint(FedoraEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void setProcessor(Processor processor) {
        this.processor = processor;
    }
}
