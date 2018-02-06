package de.qucosa.fcrepo.component;

import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.qucosa.fcrepo.fedora.api.FedoraClient;
import de.qucosa.fcrepo.fedora.api.mappings.json.DissTerms.Term;
import de.qucosa.fcrepo.fedora.api.services.FedoraOaiService;
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceFactory;
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceInstanceException;
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceInterface;
import de.qucosa.fcrepo.fedora.api.services.PersistenceService;
import de.qucosa.fcrepo.fedora.api.xmlutils.SimpleNamespaceContext;

public class MergeSetsRecordXmlProcessor<T> implements Processor {
    private ResultSet sets = null;

    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println("merge process running.");
        sets = getSets();
        FedoraEndpoint endpoint = (FedoraEndpoint) exchange.getProperty("fedora");
        FedoraClient fedoraClient = client(endpoint);
        ResultSet identifieres = (ResultSet) exchange.getIn().getBody();

        FedoraServiceInterface oaiService = FedoraServiceFactory.createService(FedoraOaiService.class);
        oaiService.setFedoraClient(fedoraClient);

        // while(identifieres.next()) {
        Map<Object, T> args = new HashMap<>();
        args.put("pid", (T) "qucosa:48670");
        // args.put("pid", (T) identifieres.getString("pid"));
        oaiService.run(oaiService, "findXmetaDiss", args);
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder
                .parse(new ByteArrayInputStream(oaiService.getServiceDataObject().toString().getBytes("UTF-8")));

        if (sets != null) {

            while (sets.next()) {

                if (sets.getString("predicate").contains("=")) {
                    String[] predicate = sets.getString("predicate").split("=");

                    if (predicate[1].contains("/")) {
                        // @todo multiple paredicate vales exists
                    } else {
                        Term term = oaiService.dissTermsConf().getTerm(predicate[0], endpoint.getMetadataPrefix());

                        if (term != null) {
                            XPath xPath = XPathFactory.newInstance().newXPath();
                            xPath.setNamespaceContext(
                                    new SimpleNamespaceContext(oaiService.dissTermsConf().getMapXmlNamespaces()));
                            
                            if (!term.getTerm().isEmpty()) {
                                String xpathExp = term.getTerm().replace("$val", predicate[1]);
                                Node node = (Node) xPath.compile(xpathExp).evaluate(document, XPathConstants.NODE);
                                
//                                node.getNodeName();
                            }
                        }
                    }
                } else {

                }
            }
        }

        System.out.println(endpoint.getMetadataPrefix());
        System.out.println(oaiService.getServiceDataObject().toString());
        // }

        fedoraClient.close();
    }

    private FedoraClient client(FedoraEndpoint endpoint) {
        FedoraClient fedoraClient = new FedoraClient(endpoint.getUser(), endpoint.getPassword());
        fedoraClient.setHost(endpoint.getHost());
        fedoraClient.setPort(endpoint.getPort());
        fedoraClient.setShema(endpoint.getShema());
        return fedoraClient;
    }

    private ResultSet getSets() {
        ResultSet results = null;

        try {
            FedoraServiceInterface service = FedoraServiceFactory.createService(PersistenceService.class);
            service.run(service, "getSets", null);
            results = service.getServiceDataObject();
        } catch (FedoraServiceInstanceException e) {
            e.printStackTrace();
        }

        return results;
    }
}
