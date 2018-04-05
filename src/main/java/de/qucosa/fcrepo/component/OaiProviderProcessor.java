package de.qucosa.fcrepo.component;

import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.w3c.dom.Document;

import de.qucosa.fcrepo.component.pojos.oaiprivider.RecordTransport;
import de.qucosa.fcrepo.component.xml.utils.DocumentXmlUtils;
import de.qucosa.xmetadissplus.DateTimeConverter;
import de.qucosa.xmetadissplus.XMetaDissMapper;

public class OaiProviderProcessor implements Processor {
    private RecordTransport xmetadiss = new RecordTransport();
    
    private RecordTransport dc = new RecordTransport();
    
    private RecordTransport epicur = new RecordTransport();
    
    private Set<RecordTransport> disseminations = new HashSet<RecordTransport>();
    
    @Override
    public void process(Exchange exchange) throws Exception {
        String metsXml = exchange.getIn().getBody().toString();
        Document metsDoc = DocumentXmlUtils.document(new ByteArrayInputStream(metsXml.getBytes("UTF-8")), true);
        
        buildXMetaDissplusObject(metsDoc);
        buildDcObject(metsDoc);
        buildEpicurObject(metsDoc);
        disseminations.add(xmetadiss);
        disseminations.add(dc);
        disseminations.add(epicur);
        
        exchange.getIn().setBody(disseminations);
    }
    
    private RecordTransport buildXMetaDissplusObject(Document metsDoc) throws Exception {
        XMetaDissMapper xMetaDiss = new XMetaDissMapper(metsDoc,
                new StreamSource(getClass().getClassLoader().getResource("mets2xmetadissplus.xsl").getPath()));
        Document result = xMetaDiss.transformXmetaDissplus();
        DocumentXmlUtils.resultXml(result);
        
        xmetadiss.setPid(xMetaDiss.pid());
        xmetadiss.setModified(DateTimeConverter.timestampWithTimezone(xMetaDiss.lastModeDate()));
        xmetadiss.setPrefix("xmetadissplus");
        xmetadiss.setData(result);
        xmetadiss.setOaiId("");
        
        return xmetadiss;
    }
    
    private RecordTransport buildDcObject(Document metsDoc) throws Exception  {
        return dc;
    }
    
    private RecordTransport buildEpicurObject(Document metsDoc) throws Exception  {
        return epicur;
    }
}
