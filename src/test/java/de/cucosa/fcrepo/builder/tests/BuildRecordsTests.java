package de.cucosa.fcrepo.builder.tests;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.junit.Test;
import org.w3c.dom.Document;

import de.qucosa.fcrepo.component.builders.RecordXmlBuilder;
import de.qucosa.fcrepo.component.mapper.DissTerms;
import de.qucosa.fcrepo.component.mapper.MetsXmlMapper;
import de.qucosa.fcrepo.component.mapper.SetsConfig;
import de.qucosa.fcrepo.component.transformers.DcDissTransformer;
import de.qucosa.fcrepo.component.xml.utils.DocumentXmlUtils;

public class BuildRecordsTests {
    private DissTerms dissTerms = new DissTerms();
    
    private SetsConfig sets = new SetsConfig();
    
    @Test
    public void buildRecordDC_Test() throws TransformerFactoryConfigurationError, Exception {
        DcDissTransformer dissTransformer = new DcDissTransformer(
                "/xml/mets2dcdata.xsl", 
                "http://##AGENT##.example.com/##PID##/content.zip", 
                "", 
                true);
        Document metsDoc = DocumentXmlUtils.document(getClass().getClassLoader().getResource("xml/hochschulschrift_TEST.xml").getPath(), true);
        Document recordTemplate = DocumentXmlUtils.document(getClass().getClassLoader().getResource("xml/record.xml").getPath(), true);
        MetsXmlMapper metsXml = new MetsXmlMapper(metsDoc, dissTerms.getMapXmlNamespaces());
        
        RecordXmlBuilder builder = new RecordXmlBuilder(dissTransformer.transformDcDiss(metsDoc), recordTemplate)
                .setMetsDocument(metsDoc)
                .setDissTerms(dissTerms)
                .setSets(sets)
                .setFormat("dc");
        Document record = builder.buildRecord(metsXml);
       
        System.out.println(DocumentXmlUtils.resultXml(record));
    }
}