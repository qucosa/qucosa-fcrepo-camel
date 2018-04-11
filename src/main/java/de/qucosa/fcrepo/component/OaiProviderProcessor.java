package de.qucosa.fcrepo.component;

import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.w3c.dom.Document;

import de.qucosa.dc.disseminator.DcDissMapper;
import de.qucosa.dissemination.epicur.EpicurDissMapper;
import de.qucosa.fcrepo.component.mapper.DissTerms;
import de.qucosa.fcrepo.component.mapper.MetsXmlMapper;
import de.qucosa.fcrepo.component.mapper.SetsConfig;
import de.qucosa.fcrepo.component.pojos.oaiprivider.RecordTransport;
import de.qucosa.fcrepo.component.xml.utils.DocumentXmlUtils;
import de.qucosa.xmetadissplus.DateTimeConverter;
import de.qucosa.xmetadissplus.XMetaDissMapper;

public class OaiProviderProcessor implements Processor {
    private RecordTransport xmetadiss = new RecordTransport();

    private RecordTransport dc = new RecordTransport();

    private RecordTransport epicur = new RecordTransport();

    private Set<RecordTransport> disseminations = new HashSet<RecordTransport>();
    
    private DissTerms dt = null;
    
    private SetsConfig sets = null;
    
    private MetsXmlMapper metsXml= null;
    
    public OaiProviderProcessor(DissTerms dt, SetsConfig sets) {
        this.dt = dt;
        this.sets = sets;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String mets = exchange.getIn().getBody().toString();
        Document metsDoc = DocumentXmlUtils.document(new ByteArrayInputStream(mets.getBytes("UTF-8")), true);
        metsXml = new MetsXmlMapper(metsDoc, dt.getMapXmlNamespaces());

        buildDcObject(metsDoc);
        disseminations.add(dc);
        
        buildXMetaDissplusObject(metsDoc);
        disseminations.add(xmetadiss);
        
        buildEpicurObject(metsDoc);
        disseminations.add(epicur);

        exchange.getIn().setBody(disseminations);
    }

    @SuppressWarnings("unused")
    private RecordTransport buildXMetaDissplusObject(Document metsDoc) throws Exception {
        XMetaDissMapper xMetaDiss = new XMetaDissMapper("http://##AGENT##.example.com/##PID##/content.zip", "", true);
        Document result = xMetaDiss.transformXmetaDissplus(metsDoc,
                new StreamSource(getClass().getClassLoader().getResource("mets2xmetadissplus.xsl").getPath()));
        XPath xPath = DocumentXmlUtils.xpath(dt.getMapXmlNamespaces());

        xmetadiss.setPid(metsXml.pid());
        xmetadiss.setModified(DateTimeConverter.timestampWithTimezone(metsXml.lastModDate()));
        xmetadiss.setPrefix("xmetadissplus");
        xmetadiss.setData(result);
        xmetadiss.setOaiId("");

        return xmetadiss;
    }

    @SuppressWarnings("unused")
    private RecordTransport buildDcObject(Document metsDoc) throws Exception {
        DcDissMapper dcDissMapper = new DcDissMapper("/mets2dcdata.xsl");
        Document result = dcDissMapper.transformDcDiss(metsDoc);
        XPath xPath = DocumentXmlUtils.xpath(dt.getMapXmlNamespaces());
        DocumentXmlUtils.resultXml(metsDoc);
        
        dc.setPid(metsXml.pid());
        dc.setModified(DateTimeConverter.timestampWithTimezone(metsXml.lastModDate()));
        dc.setPrefix("dc");
        dc.setData(result);
        dc.setOaiId("");

        return dc;
    }

    @SuppressWarnings("unused")
    private RecordTransport buildEpicurObject(Document metsDoc) throws Exception {
        EpicurDissMapper mapper = new EpicurDissMapper("http://test.##AGENT##.qucosa.de/id/##PID##", "", "", true);
        Document epicurRes = mapper.transformEpicurDiss(metsDoc);
        XPath xPath = DocumentXmlUtils.xpath(dt.getMapXmlNamespaces());
        
        epicur.setPid(metsXml.pid());
        epicur.setModified(DateTimeConverter.timestampWithTimezone(metsXml.lastModDate()));
        epicur.setPrefix("epicur");
        epicur.setData(epicurRes);
        epicur.setOaiId("");
        
        return epicur;
    }
    
    private Document buildRecord(Document dissemination, MetsXmlMapper metsXml) {
        Document record = null;
        Document recordTemplate = DocumentXmlUtils.document(getClass().getClassLoader().getResource("record.xml").getPath(), true);
        
        return record;
    }
}
