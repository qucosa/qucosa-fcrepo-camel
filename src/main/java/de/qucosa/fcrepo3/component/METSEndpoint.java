package de.qucosa.fcrepo3.component;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.spi.UriParam;
import org.apache.http.impl.client.CloseableHttpClient;
import org.w3c.dom.NodeList;

public class METSEndpoint extends AbstractFcrepo3Endpoint {
    @UriParam
    private String supplement;
    
    public METSEndpoint(String endpointUri, Component component, Fcrepo3Configuration configuration) {
        super(endpointUri, component);
        setConfiguration(configuration);
    }
    
    @Override
    public Consumer createConsumer(Processor arg0) {
        return null;
    }

    @Override
    public Producer createProducer() {
        return new DefaultProducer(this) {

            @Override
            public void process(Exchange exchange) throws Exception {
                CloseableHttpClient fedoraClient = fedoraClient();
                NodeList list = (NodeList) exchange.getIn().getBody();
                String pid = list.item(0).getNodeValue();
                String metsXml = loadFromFedora(AbstractFcrepo3Endpoint.METS_URL, getSchema(), getHost(), getPort(), pid);
                exchange.getIn().setBody(metsXml);
                fedoraClient.close();
            }
        };
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
    
    public String getSupplement() {
        return supplement;
    }

    public void setSupplement(String supplement) {
        this.supplement = supplement;
    }
}
