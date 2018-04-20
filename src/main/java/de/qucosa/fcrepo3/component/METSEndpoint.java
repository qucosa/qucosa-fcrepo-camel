package de.qucosa.fcrepo3.component;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;

public class METSEndpoint extends AbstractFcrepo3Endpoint {
    
    public METSEndpoint(String endpointUri, Component component, Fcrepo3Configuration configuration) {
        super(endpointUri, component);
        setConfiguration(configuration);
    }
    
    @Override
    public Consumer createConsumer(Processor arg0) throws Exception {
        return null;
    }

    @Override
    public Producer createProducer() throws Exception {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
