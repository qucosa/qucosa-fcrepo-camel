package de.qucosa.fcrepo.component.mapper;

import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import de.qucosa.fcrepo.component.xml.utils.DocumentXmlUtils;

public class MetsXmlMapper {
    private Document metsDoc = null;
    
    private XPath xPath = null;
    
    public MetsXmlMapper(Document metsDoc, Map<String, String> namespaces) {
        this.metsDoc = metsDoc;
        xPath = DocumentXmlUtils.xpath(namespaces);
    }
    
    public String pid() throws XPathExpressionException {
        String pid = (String) xPath.compile("//mets:mets/@OBJID").evaluate(metsDoc, XPathConstants.STRING);
        return pid;
    }
    
    public String lastModDate() throws XPathExpressionException {
        String lastModDate = (String) xPath.compile("//mets:mets/mets:metsHdr/@LASTMODDATE").evaluate(metsDoc, XPathConstants.STRING);
        return lastModDate;
    }
    
    public String agentName() throws XPathExpressionException {
        String agent = (String) xPath.compile("//mets:agent[@ROLE='EDITOR' and @TYPE='ORGANIZATION']/mets:name[1]").evaluate(metsDoc, XPathConstants.STRING);
        return agent;
    }
}
