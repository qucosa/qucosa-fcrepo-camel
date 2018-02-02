package de.qucosa.fcrepo.component.endpoint.defenitions;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.impl.DefaultScheduledPollConsumer;

import de.qucosa.fcrepo.component.EndpointDefAbstract;
import de.qucosa.fcrepo.component.EndpointDefAnnotation;
import de.qucosa.fcrepo.component.EndpointDefInterface;
import de.qucosa.fcrepo.component.FedoraEndpoint;
import de.qucosa.fcrepo.fedora.api.FedoraClient;

@EndpointDefAnnotation(isConsumer = true, isProducer = true)
public class OaiPmh extends EndpointDefAbstract implements EndpointDefInterface {
    @Override
    public Consumer getConsumer() {
        return new OaiPmhConsumer(endpoint, processor);
    }
    
    @Override
    public Producer getProducer() {
        return new DefaultProducer(endpoint) {
            
            @Override
            public void process(Exchange exchange) throws Exception {
                // TODO Auto-generated method stub
            }
        };
    }

    public static class OaiPmhConsumer extends DefaultScheduledPollConsumer {
        private FedoraEndpoint endpoint;
        
        private Processor processor;
        
        private FedoraClient fedoraClient;
        
        public OaiPmhConsumer(FedoraEndpoint endpoint, Processor processor) {
            super(endpoint, processor);
            this.endpoint = endpoint;
            this.processor = processor;
            this.fedoraClient = new FedoraClient(this.endpoint.getUser(), this.endpoint.getPassword());
        }
        
        @Override
        protected int poll() throws Exception {
            return poll(null);
        }
        
        protected int poll(String type) {
            return 0;
        }
    }
}
