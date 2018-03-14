package de.qucosa.fcrepo.component.endpoint.defenitions;

import java.io.ByteArrayInputStream;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultProducer;
import org.w3c.dom.Document;

import de.qucosa.fcrepo.component.EndpointDefAbstract;
import de.qucosa.fcrepo.component.EndpointDefInterface;
import de.qucosa.fcrepo.component.xml.utils.DocumentXmlUtils;
import de.qucosa.fcrepo.component.xml.utils.SimpleNamespaceContext;

public class IdentifireQue extends EndpointDefAbstract implements EndpointDefInterface {
    @Override
    public Consumer getConsumer() {
        return new DefaultConsumer(endpoint, processor) {
            @Override
            protected void doStart() throws Exception {
                super.doStart();
                Exchange exchange = endpoint.createExchange();
//                System.out.println("IDs: " + exchange.getIn().getBody());
            }
        };
    }

    @Override
    public Producer getProducer() {
        return new DefaultProducer(endpoint) {

            @Override
            public void process(Exchange exchange) throws Exception {

                if (endpoint.getSource().equals("oai")) {
                    Set<String> identifires = (Set<String>) exchange.getIn().getBody();
                    
                    for(String ident : identifires) {
                        endpoint.getIdentifires().add(ident);
                    }
                }
                
                if (endpoint.getSource().equals("jms")) {
                    Document document = DocumentXmlUtils.document(new ByteArrayInputStream(exchange.getIn().getBody().toString().getBytes("UTF-8")), false);
                    XPath xPath = DocumentXmlUtils.xpath();
                    xPath.setNamespaceContext(new SimpleNamespaceContext(endpoint.getConfiguration().getDissConf().getMapXmlNamespaces()));
                    String pid = (String) xPath.compile("//summary[@type=\"text\"]/text()").evaluate(document, XPathConstants.STRING);
                    endpoint.getIdentifires().add(pid);
                }
                
                exchange.getIn().setBody(endpoint.getIdentifires());
            }
        };
    }

}
