package de.qucosa.fcrepo.component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.qucosa.fcrepo.fedora.api.FedoraClient;
import de.qucosa.fcrepo.fedora.api.mappings.json.DissTerms.Term;
import de.qucosa.fcrepo.fedora.api.pojos.Record;
import de.qucosa.fcrepo.fedora.api.services.FedoraOaiService;
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceFactory;
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceInstanceException;
import de.qucosa.fcrepo.fedora.api.services.FedoraServiceInterface;
import de.qucosa.fcrepo.fedora.api.services.PersistenceService;
import de.qucosa.fcrepo.fedora.api.utils.DateTimeConverter;
import de.qucosa.fcrepo.fedora.api.xmlutils.SimpleNamespaceContext;

public class MergeSetsRecordXmlProcessor<T> implements Processor {
    private FedoraEndpoint endpoint = null;

    private FedoraServiceInterface oaiService = null;
    
    @SuppressWarnings({ "unchecked" })
    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println("Process MergeSetsRecordXmlProcessor is started.");
        endpoint = (FedoraEndpoint) exchange.getProperty("fedora");
        oaiService = oaiService();
        ResultSet formats = formats();
        ResultSet identifieres = (ResultSet) exchange.getIn().getBody();
        Map<Object, T> args = new HashMap<>();
        Set<Record> records = new HashSet<>();
        
        while (formats.next()) {
            String format = formats.getString("mdprefix");
            System.out.println(format);
            
            if (format != null && !format.isEmpty()) {
                int cnt = 0;
                endpoint.setMetadataPrefix(format);
                
                switch(format) {
                    case "xmetadissplus":
                        while (identifieres.next()) {
                            cnt++;
                            args.put("pid", (T) identifieres.getString("pid"));
                            args.put("method", (T) formats.getString("method"));
                            oaiService.run(oaiService, "findDissemination", args);
                            System.out.println(cnt + " - " + format + " / " + identifieres.getString("identifier"));
                            Document xmetadissplus = writeMetadata(document(), getSets(), identifieres, formats);
                            Record record = new Record();
                            record.setIdentifierId(identifieres.getLong("id"));
                            record.setModDate(DateTimeConverter.sqlDate(xmetadissplus.getElementsByTagName("dcterms:modified").item(0).getTextContent()));
                            record.setXmlData(resultXml(xmetadissplus));
                            record.setFormat(formats.getLong("id"));
                            records.add(record);
                        }
                        
                        args.clear();
                        break;
                    case "oai_dc":
                        while (identifieres.next()) {
                            cnt++;
                            args.put("pid", (T) identifieres.getString("pid"));
                            args.put("method", (T) formats.getString("method"));
                            oaiService.run(oaiService, "findDissemination", args);
                            System.out.println(cnt + " - " + format + " / " + identifieres.getString("identifier"));
                            Document oaidc = writeMetadata(document(), getSets(), identifieres, formats);
                            Record oai = new Record();
                            oai.setIdentifierId(identifieres.getLong("id"));
                            oai.setXmlData(resultXml(oaidc));
                            oai.setFormat(formats.getLong("id"));
                            records.add(oai);
                        }
                        
                        args.clear();
                        break;
                }
            }
        }
        
        formats.close();
        identifieres.close();
        getSets().close();
        
        exchange.getIn().setBody(records);
        client(endpoint).close();
    }

    private FedoraClient client(FedoraEndpoint endpoint) {
        FedoraClient fedoraClient = new FedoraClient(endpoint.getUser(), endpoint.getPassword());
        fedoraClient.setHost(endpoint.getHost());
        fedoraClient.setPort(endpoint.getPort());
        fedoraClient.setShema(endpoint.getShema());
        return fedoraClient;
    }

    private FedoraServiceInterface oaiService() {
        FedoraServiceInterface oaiService = null;

        try {
            oaiService = FedoraServiceFactory.createService(FedoraOaiService.class);
            oaiService.setFedoraClient(client(endpoint));
        } catch (FedoraServiceInstanceException e) {
            e.printStackTrace();
        }

        return oaiService;
    }

    private Document document() throws DOMException, SQLException {
        Document document = null;

        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            document = documentBuilder
                    .parse(new ByteArrayInputStream(oaiService.getServiceDataObject().toString().getBytes("UTF-8")));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return document;
    }
    
    private Document writeMetadata(Document document, ResultSet sets, ResultSet identifiers, ResultSet formats) throws DOMException, SQLException {
        Node record = document.createElement("record");
        Node header = record.appendChild(document.createElement("header"));
        
        Node identifierNode = document.createElement("identifier");
        identifierNode.appendChild(document.createTextNode(identifiers.getString("identifier")));
        header.appendChild(identifierNode);
        
        Node datestamp = document.createElement("datestamp");
        datestamp.appendChild(document.createTextNode(DateTimeConverter.sqlTimestampToString(identifiers.getTimestamp("datestamp"))));
        header.appendChild(datestamp);
        
        Node metadata = record.appendChild(document.createElement("metadata"));
        metadata.appendChild(document.getDocumentElement());
        
        document.appendChild(record);
        
        try {
            writeHeader(sets, document);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        
        return document;
    }
    
    @SuppressWarnings("unused")
    private Document epicure(Document document, ResultSet sets, ResultSet identifiers, ResultSet formats) {
        return document;
    }

    private XPath xpath() {
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(new SimpleNamespaceContext(oaiService.dissTermsConf().getMapXmlNamespaces()));
        return xPath;
    }
    
    private void writeHeader(ResultSet sets, Document document) throws XPathExpressionException, DOMException, SQLException {
        Node header = document.getElementsByTagName("header").item(0);
        XPath xPath = xpath();
        
        if (sets != null) {

            while (sets.next()) {

                if (sets.getString("predicate").contains("=")) {
                    String[] predicate = sets.getString("predicate").split("=");

                    if (predicate[1].contains("/")) {
                        // @todo multiple paredicate vales exists
                    } else {
                        Term term = oaiService.dissTermsConf().getTerm(predicate[0], endpoint.getMetadataPrefix());

                        if (term != null && !term.getTerm().isEmpty()) {
                            Node node = (Node) xPath.compile(term.getTerm().replace("$val", predicate[1]))
                                    .evaluate(document, XPathConstants.NODE);

                            if (node != null) {
                                Node setspecnode = document.createElement("setSpec");
                                setspecnode
                                        .appendChild(document.createTextNode(sets.getString("setspec").toString()));
                                header.appendChild(setspecnode);
                                setspecnode = null;
                            }
                        }
                    }
                } else {

                }
            }
        }
    }
    
    private String resultXml(Document document) throws IOException, SAXException {
        OutputFormat outputFormat = new OutputFormat(document);
        outputFormat.setOmitXMLDeclaration(true);
        StringWriter stringWriter = new StringWriter();
        XMLSerializer serialize = new XMLSerializer(stringWriter, outputFormat);
        serialize.serialize(document);
        return stringWriter.toString();
    }
    
    private ResultSet formats() {
        ResultSet formats = null;
        
        try {
            FedoraServiceInterface service = FedoraServiceFactory.createService(PersistenceService.class);
            service.run(service, "findFormats", null);
            formats = service.getServiceDataObject();
        } catch (FedoraServiceInstanceException e) {
            e.printStackTrace();
        }
        
        return formats;
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
