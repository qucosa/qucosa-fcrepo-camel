package de.qucosa.fcrepo.component.endpoint.defenitions;

import static de.qucosa.fcrepo.fedora.api.FedoraApiConstans.END_OF_INPUT;
import static de.qucosa.fcrepo.fedora.api.FedoraApiConstans.FEDORA_OBJECT_XML_URI;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qucosa.fcrepo.component.EndpointDefAbstract;
import de.qucosa.fcrepo.component.EndpointDefAnnotation;
import de.qucosa.fcrepo.component.EndpointDefInterface;
import de.qucosa.fcrepo.component.FedoraEndpoint;
import de.qucosa.fcrepo.fedora.api.FedoraClient;

@EndpointDefAnnotation(isConsumer = true, isProducer = false)
public class FindObjects extends EndpointDefAbstract implements EndpointDefInterface {
    @Override
    public Consumer getConsumer() {
        endpoint.getObjectService();
        return new FindObjectsConsumer(endpoint, processor);
    }
    
    @Override
    public Producer getProducer() {
        return null;
    }
    
    public static class FindObjectsConsumer extends DefaultScheduledPollConsumer {
        @SuppressWarnings("unused")
        private Logger logger = LoggerFactory.getLogger(FindObjectsConsumer.class);

        private FedoraEndpoint endpoint;

        private Processor processor;

        private FedoraClient client = null;
        
        public FindObjectsConsumer(FedoraEndpoint endpoint, Processor processor) {
            super(endpoint, processor);
            this.endpoint = endpoint;
            this.processor = processor;
            
            this.endpoint.setPort("4711");
        }
        
        @Override
        protected int poll() throws Exception {
            String data = client.get(FEDORA_OBJECT_XML_URI,
                    END_OF_INPUT, endpoint.getShema(), endpoint.getHost(),
                    endpoint.getPort(), "qucosa:48674");
            Exchange exchange = endpoint.createExchange();
            exchange.getIn().setBody(data);
            processor.process(exchange);
            return 0;
        }

        @Override
        protected void doStart() throws Exception {
            super.doStart();
            endpoint.setPort("4711");
            client = new FedoraClient(endpoint.getUser(), endpoint.getPassword());
        }

        @Override
        protected void doStop() throws Exception {
            client.close();
            super.doStop();
        }
    }
}
