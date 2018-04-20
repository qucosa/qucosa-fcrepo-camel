package de.qucosa.fcrepo3.component;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriParam;

public class METSEndpoint extends DefaultEndpoint {
    @UriParam
    private Fcrepo3Configuration configuration;
    
    public METSEndpoint(String endpointUri, Component component, Fcrepo3Configuration configuration) {
        super(endpointUri, component);
        this.configuration = configuration;
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
