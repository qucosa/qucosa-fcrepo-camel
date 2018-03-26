package de.qucosa.fcrepo.component.endpoint.defenitions;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultProducer;
import org.apache.http.impl.client.CloseableHttpClient;

import de.qucosa.fcrepo.component.EndpointDefAbstract;
import de.qucosa.fcrepo.component.EndpointDefInterface;
import de.qucosa.fcrepo.component.FedoraEndpoint;

public class IdentifireQue extends EndpointDefAbstract implements EndpointDefInterface {
    @Override
    public Consumer getConsumer() {
        return null;
    }

    @Override
    public Producer getProducer() {
        return new DefaultProducer(endpoint) {
            @Override
            public void process(Exchange exchange) throws Exception {
                CloseableHttpClient fedoraClient = endpoint.fedoraClient();
                String pid = exchange.getIn().getBody().toString();
                String metsXml = endpoint.loadFromFedora(FedoraEndpoint.METS_URL, endpoint.getShema(), endpoint.getHost(), endpoint.getPort(), pid);
                exchange.getIn().setBody(metsXml);
                fedoraClient.close();
            }
        };
    }

}
