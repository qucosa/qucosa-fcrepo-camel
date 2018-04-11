package de.qucosa.fcrepo.component.builders;

import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.qucosa.fcrepo.component.mapper.DissTerms;
import de.qucosa.fcrepo.component.mapper.DissTerms.Term;
import de.qucosa.fcrepo.component.mapper.MetsXmlMapper;
import de.qucosa.fcrepo.component.mapper.SetsConfig;
import de.qucosa.fcrepo.component.mapper.SetsConfig.Set;
import de.qucosa.fcrepo.component.xml.utils.DocumentXmlUtils;

public class RecordXmlBuilder {
    private Document dissemination = null;
    
    private Document recordTemplate = null;
    
    private Document metsDoc = null;
    
    private DissTerms dissTerms = null;
    
    private SetsConfig sets = null;
    
    private String format = null;
    
    public RecordXmlBuilder(Document dissemination, Document recordTemplate) {
        this.dissemination = dissemination;
        this.recordTemplate = recordTemplate;
    }
    
    public Document buildRecord(MetsXmlMapper metsXml) throws XPathExpressionException {
        Node importDissemination = recordTemplate.importNode(dissemination.getDocumentElement(), true);
        metadata().appendChild(importDissemination);
        recordIdentifiere().appendChild(recordTemplate.createTextNode(metsXml.pid()));
        recordDatestamp().appendChild(recordTemplate.createTextNode(metsXml.lastModDate()));
        appendSetSpecs();
        return recordTemplate;
    }
    
    public RecordXmlBuilder setMetsDocument(Document metsDoc) {
        this.metsDoc = metsDoc;
        return this;
    }
    
    public RecordXmlBuilder setDissTerms(DissTerms dissTerms) {
        this.dissTerms = dissTerms;
        return this;
    }

    public RecordXmlBuilder setSets(SetsConfig sets) {
        this.sets = sets;
        return this;
    }
    
    public RecordXmlBuilder setFormat(String format) {
        this.format = format;
        return this;
    }
    
    private Node recordHeader() throws XPathExpressionException {
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        Node header = (Node) xPath.compile("//record/header").evaluate(recordTemplate, XPathConstants.NODE);
        return header;
    }
    
    private Node recordIdentifiere() throws XPathExpressionException {
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        Node identifier = (Node) xPath.compile("//record/header/identifier").evaluate(recordTemplate, XPathConstants.NODE);
        return identifier;
    }
    
    private Node recordDatestamp() throws XPathExpressionException {
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        Node datestamp = (Node) xPath.compile("//record/header/datestamp").evaluate(recordTemplate, XPathConstants.NODE);
        return datestamp;
    }
    
    private Element metadata() throws XPathExpressionException {
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        Element metadata = (Element) xPath.compile("//record/metadata").evaluate(recordTemplate, XPathConstants.NODE);
        return metadata;
    }
    
    private boolean matchTerm(String key, String value) throws XPathExpressionException {
        Term term = dissTerms.getTerm(key, format);
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        Node node = null;
        
        if (term != null) {
            
            if (!term.getTerm().isEmpty()) {
                node = (Node) xPath.compile(term.getTerm().replace("$val", value)).evaluate(metsDoc, XPathConstants.NODE);
            }
        }
        
        return (node != null) ? true : false;
    }
    
    private void addSetSpec(Node header, Set set) {
        Node setSpecElem = recordTemplate.createElement("setSpec");
        setSpecElem.appendChild(recordTemplate.createTextNode(set.getSetSpec()));
        header.appendChild(setSpecElem);
    }
    
    private void appendSetSpecs() throws XPathExpressionException {
        Node header = recordHeader();
        List<Set> setObjects = sets.getSetObjects();
        
        for (Set setObj : setObjects) {
            String predicateKey = null;
            String predicateValue = null;
            
            if (setObj.getPredicate() != null && !setObj.getPredicate().isEmpty()) {
                
                if (setObj.getPredicate().contains("=")) {
                    String[] predicate = setObj.getPredicate().split("=");
                    predicateKey = predicate[0];
                    predicateValue = predicate[1];
                    
                    if (!predicateValue.contains("/")) {
                        
                        if (matchTerm(predicateKey, predicateValue)) {
                            addSetSpec(header, setObj);
                        }
                    } else {
                        String[] predicateValues = predicateValue.split("/");
                        
                        if (predicateValues.length > 0) {
                            
                        }
                    }
                } else {
                    predicateKey = setObj.getPredicate();
                }
            }
        }
    }
}
