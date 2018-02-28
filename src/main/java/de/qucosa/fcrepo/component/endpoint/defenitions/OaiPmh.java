package de.qucosa.fcrepo.component.endpoint.defenitions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultProducer;
import org.apache.http.impl.client.CloseableHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.fcrepo.component.EndpointDefAbstract;
import de.qucosa.fcrepo.component.EndpointDefAnnotation;
import de.qucosa.fcrepo.component.EndpointDefInterface;
import de.qucosa.fcrepo.component.FedoraEndpoint;
import de.qucosa.fcrepo.component.xml.utils.DocumentXmlUtils;
import de.qucosa.fcrepo.component.xml.utils.SimpleNamespaceContext;
import de.qucosa.fcrepo.fedora.api.mappings.xml.Identifier;

@EndpointDefAnnotation(isConsumer = true, isProducer = true)
public class OaiPmh<T> extends EndpointDefAbstract implements EndpointDefInterface {
    final Set<Identifier> identifiers = new HashSet<>();
    
    @Override
    public Consumer getConsumer() {
        return new DefaultConsumer(endpoint, processor) {
            @SuppressWarnings("unused")
            private CloseableHttpClient fedoraClient;
            
            @Override
            protected void doStart() throws Exception {
                super.doStart();
                fedoraClient = endpoint.fedoraClient();
                buildObjects(null);
                Exchange exchange = endpoint.createExchange();
                exchange.getIn().setBody(identifiers);
                processor.process(exchange);
            }
        };
    }
    
    @Override
    public Producer getProducer() {
        return new DefaultProducer(endpoint) {
            
            @Override
            public void process(Exchange exchange) throws Exception {
                ObjectMapper om = new ObjectMapper();
                String json = om.writeValueAsString(exchange.getIn().getBody());
                URL url = new URL("http://localhost:8080/qucosa-oai-provider/identifieres/add");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(json.getBytes());
                outputStream.flush();
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String output = null;
                
                while ((output = reader.readLine()) != null) {
                    System.out.println(output);
                }
                
                connection.disconnect();
            }
        };
    }
    
    private String xml(String resumptionToken) {
        return (resumptionToken == null)
            ? endpoint.loadFromFedora(FedoraEndpoint.OAIPMH_LISTIDENTIFIERS_URL_WITHOUT_RESUMPTIONTOKEN, endpoint.getShema(), endpoint.getHost(), endpoint.getPort())
            : endpoint.loadFromFedora(FedoraEndpoint.OAIPMH_LISTIDENTIFIERS_URL_WITH_RESUMPTIONTOKEN, endpoint.getShema(), endpoint.getHost(), endpoint.getPort(), resumptionToken);
    }
    
    private void buildObjects(String resumptionToken) {
        String xml = xml(resumptionToken);
        
        try {
            Document document = DocumentXmlUtils.document(new ByteArrayInputStream(xml.getBytes("UTF-8")), false);
            XPath xPath = DocumentXmlUtils.xpath();
            xPath.setNamespaceContext(new SimpleNamespaceContext(endpoint.getConfiguration().getDissConf().getMapXmlNamespaces()));
            Node rst = document.getElementsByTagName("resumptionToken").item(0);
            NodeList headers = (NodeList) xPath.compile("//header").evaluate(document, XPathConstants.NODESET);
            
            for (int i = 0; i < headers.getLength(); i++) {
                Node header = headers.item(i);
                header.getChildNodes();
                Identifier identifier = new Identifier();
                identifier.setIdentifier((String) xPath.compile("./identifier/text()").evaluate(header, XPathConstants.STRING));
                identifier.setDatestamp((String) xPath.compile("./datestamp/text()").evaluate(header, XPathConstants.STRING));
                identifiers.add(identifier);
            }
            
            if (rst != null) {
                buildObjects(rst.getTextContent());
            }
        } catch (UnsupportedEncodingException | XPathExpressionException e) {
            e.printStackTrace();
        }
    }
}
