package de.qucosa.fcrepo.component;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;

public interface EndpointDefInterface {
    public void setEndpoint(FedoraEndpoint endpoint);
    
    public void setProcessor(Processor processor);
    
    public Consumer getConsumer();
    
    public Producer getProducer();
}
