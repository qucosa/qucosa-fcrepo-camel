package de.qucosa.fcrepo.component.transformers;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.w3c.dom.Document;

import de.qucosa.fcrepo.component.xml.utils.DocumentXmlUtils;
import de.qucosa.fcrepo.component.xml.utils.SimpleNamespaceContext;

public class DcDissTransformer {
    private final StreamSource xslSource;
    
    private Document metsDoc = null;

    private String transferUrlPattern;

    private boolean transferUrlPidencode;
    
    @SuppressWarnings({ "serial", "unused" })
    private Map<String, String> agentNameSubstitutions = new HashMap<String, String>() {
        {
            put("ubc", "monarch");
        }
        {   
            put("ubl", "ul");
        }
    };
    
    public DcDissTransformer(String xsltStylesheetResourceName, String transferUrlPattern, String agentNameSubstitutions, boolean transferUrlPidencode) {
        this.transferUrlPattern = transferUrlPattern;
        this.transferUrlPidencode = transferUrlPidencode;
        this.agentNameSubstitutions = decodeSubstitutions(agentNameSubstitutions);
        xslSource = new StreamSource(this.getClass().getResourceAsStream(xsltStylesheetResourceName));
    }
    
    @SuppressWarnings("serial")
    public Document transformDcDiss(Document metsDocument) throws TransformerException, XPathExpressionException, UnsupportedEncodingException {
        metsDoc = metsDocument;
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        
        Map<String, String> values = new LinkedHashMap<String, String>() {
            {
                put("AGENT", extractAgent());
            }
            {
                put("PID", extractPid());
            }
        };
        
        StrSubstitutor substitutor = new StrSubstitutor(values, "##", "##");
        String transferUrl = substitutor.replace(transferUrlPattern);
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(xslSource);
        transformer.setParameter("transfer_url", transferUrl);
        transformer.transform(new DOMSource(metsDocument), streamResult);
        
        Document transformDoc = DocumentXmlUtils.document(new ByteArrayInputStream(stringWriter.toString().getBytes("UTF-8")), true);
        
        return transformDoc;
    }
    
    private String extractAgent() throws XPathExpressionException {
        String agent = null;
        XPath xPath = xpath();
        agent = (String) xPath.compile("//mets:agent[@ROLE='EDITOR' and @TYPE='ORGANIZATION']/mets:name[1]")
                .evaluate(metsDoc, XPathConstants.STRING);
        return agent;
    }

    private String extractPid() throws XPathExpressionException {
        String pid = null;
        
        if (transferUrlPidencode) {
            XPath xPath = xpath();
            pid = (String) xPath.compile("//mets:mets/@OBJID").evaluate(metsDoc, XPathConstants.STRING);
        }
        
        return pid;
    }
    
    private Map<String, String> decodeSubstitutions(String parameterValue) {
        HashMap<String, String> result = new HashMap<String, String>();

        if (parameterValue != null && !parameterValue.isEmpty()) {

            for (String substitution : parameterValue.split(";")) {
                String[] s = substitution.split("=");
                result.put(s[0].trim(), s[1].trim());
            }
        }

        return result;
    }

    @SuppressWarnings("serial")
    private XPath xpath() {
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(new SimpleNamespaceContext(new HashMap<String, String>() {
            {
                put("mets", "http://www.loc.gov/METS/");
            }
        }));
        return xPath;
    }
}
