/*
 * Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.qucosa.oaiprovider.component;

import de.qucosa.oaiprovider.component.builders.RecordXmlBuilder;
import de.qucosa.oaiprovider.component.model.DissTerms;
import de.qucosa.fcrepo.component.mapper.MetsXmlMapper;
import de.qucosa.oaiprovider.component.model.SetsConfig;
import de.qucosa.oaiprovider.component.model.RecordTransport;
import de.qucosa.transformers.DcDissTransformer;
import de.qucosa.transformers.XMetaDissTransformer;
import de.qucosa.utils.DateTimeConverter;
import de.qucosa.utils.DocumentXmlUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.w3c.dom.Document;

import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import java.util.HashSet;
import java.util.Set;

//import de.qucosa.dissemination.epicur.EpicurDissMapper;

public class OaiProviderProcessor implements Processor {
    private static final String RECORD_TEMPLATE_FILE = "record.xml";
    private RecordTransport xmetadiss = new RecordTransport();
    private RecordTransport dc = new RecordTransport();
    private RecordTransport epicur = new RecordTransport();
    private Set<RecordTransport> disseminations = new HashSet<RecordTransport>();
    private DissTerms dt = null;
    private SetsConfig sets = null;
    private MetsXmlMapper metsXml = null;

    public OaiProviderProcessor() {
        this.dt = new DissTerms();
        this.sets = new SetsConfig();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String mets = exchange.getIn().getBody().toString();
        Document metsDoc = DocumentXmlUtils.document(new ByteArrayInputStream(mets.getBytes("UTF-8")), true);
        DocumentXmlUtils.resultXml(metsDoc);
        metsXml = new MetsXmlMapper(metsDoc, dt.getMapXmlNamespaces());

        //TODO extract transformations to camel route
        buildDcObject(metsDoc);
        disseminations.add(dc);

        buildXMetaDissplusObject(metsDoc);
        disseminations.add(xmetadiss);

//        buildEpicurObject(metsDoc);
        disseminations.add(epicur);

        exchange.getIn().setBody(disseminations);
    }

    @SuppressWarnings("unused")
    private RecordTransport buildXMetaDissplusObject(Document metsDoc) throws Exception {
        XMetaDissTransformer transformer = new XMetaDissTransformer("http://##AGENT##.example.com/##PID##/content.zip", "", true);
        Document result = transformer.transformXmetaDissplus(metsDoc,
                new StreamSource(getClass().getClassLoader().getResource("mets2xmetadissplus.xsl").getPath()));
        XPath xPath = DocumentXmlUtils.xpath(dt.getMapXmlNamespaces());
        DocumentXmlUtils.resultXml(buildRecord(result, metsDoc, "xmetadissplus"));

        xmetadiss.setPid(metsXml.pid());
        xmetadiss.setModified(DateTimeConverter.timestampWithTimezone(metsXml.lastModDate()));
        xmetadiss.setPrefix("xmetadissplus");
        xmetadiss.setData(buildRecord(result, metsDoc, "xmetadissplus"));
        xmetadiss.setOaiId("");

        return xmetadiss;
    }

    @SuppressWarnings("unused")
    private RecordTransport buildDcObject(Document metsDoc) throws Exception {
        DcDissTransformer transformer = new DcDissTransformer(
                "/mets2dcdata.xsl", "http://##AGENT##.example.com/##PID##/content.zip",
                "",
                true);
        Document result = transformer.transformDcDiss(metsDoc);
        XPath xPath = DocumentXmlUtils.xpath(dt.getMapXmlNamespaces());
        DocumentXmlUtils.resultXml(buildRecord(result, metsDoc, "dc"));

        dc.setPid(metsXml.pid());
        dc.setModified(DateTimeConverter.timestampWithTimezone(metsXml.lastModDate()));
        dc.setPrefix("dc");
        dc.setData(buildRecord(result, metsDoc, "dc"));
        dc.setOaiId("");

        return dc;
    }

/*
    @SuppressWarnings("unused")
    private RecordTransport buildEpicurObject(Document metsDoc) throws Exception {
        EpicurDissMapper mapper = new EpicurDissMapper("http://test.##AGENT##.qucosa.de/id/##PID##", "", "", true);
        Document epicurRes = mapper.transformEpicurDiss(metsDoc);
        XPath xPath = DocumentXmlUtils.xpath(dt.getMapXmlNamespaces());
        DocumentXmlUtils.resultXml(buildRecord(epicurRes, metsDoc, "epicur"));

        epicur.setPid(metsXml.pid());
        epicur.setModified(DateTimeConverter.timestampWithTimezone(metsXml.lastModDate()));
        epicur.setPrefix("epicur");
        epicur.setData(buildRecord(epicurRes, metsDoc, "epicur"));
        epicur.setOaiId("");
        
        return epicur;
    }
*/

    private Document buildRecord(Document dissemination, Document metsDoc, String format) throws XPathExpressionException {
        Document recordTemplate = DocumentXmlUtils.document(getClass().getClassLoader().getResource(RECORD_TEMPLATE_FILE).getPath(), true);
        RecordXmlBuilder builder = new RecordXmlBuilder(dissemination, recordTemplate)
                .setMetsDocument(metsDoc)
                .setDissTerms(dt)
                .setSets(sets)
                .setFormat(format);

        return builder.buildRecord(metsXml);
    }
}
