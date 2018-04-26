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

import de.qucosa.fcrepo3.component.mapper.MetsXmlMapper;
import de.qucosa.oaiprovider.component.model.DissTerms;
import de.qucosa.oaiprovider.component.model.RecordTransport;
import de.qucosa.oaiprovider.component.model.SetsConfig;
import de.qucosa.transformers.DcDissTransformer;
import de.qucosa.transformers.XMetaDissTransformer;
import de.qucosa.utils.DateTimeConverter;
import de.qucosa.utils.DocumentXmlUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import de.qucosa.dissemination.epicur.EpicurDissMapper;

public class OaiProviderProcessor implements Processor {
    private static final String RECORD_TEMPLATE_FILE = "oaiprovider/record.xml";
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
        Document metsDoc = (Document) exchange.getIn().getBody();
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

    private RecordTransport buildXMetaDissplusObject(Document metsDoc) throws Exception {
        XMetaDissTransformer transformer = new XMetaDissTransformer("http://##AGENT##.example.com/##PID##/content.zip", "", true);
        Document result = transformer.transformXmetaDissplus(metsDoc,
                new StreamSource(getClass().getClassLoader().getResource("xslt/mets2xmetadissplus.xsl").getPath()));
        XPath xPath = DocumentXmlUtils.xpath(dt.getMapXmlNamespaces());

        xmetadiss.setPid(metsXml.pid());
        xmetadiss.setModified(DateTimeConverter.timestampWithTimezone(metsXml.lastModDate()));
        xmetadiss.setPrefix("xmetadissplus");
        xmetadiss.setData(result);
        xmetadiss.setOaiId("");
        xmetadiss.setSets(getSetSpecs("xmetadissplus", result));

        return xmetadiss;
    }

    private RecordTransport buildDcObject(Document metsDoc) throws Exception {
        DcDissTransformer transformer = new DcDissTransformer(
                "/xslt/mets2dcdata.xsl", "http://##AGENT##.example.com/##PID##/content.zip",
                "",
                true);
        Document result = transformer.transformDcDiss(metsDoc);
        XPath xPath = DocumentXmlUtils.xpath(dt.getMapXmlNamespaces());

        dc.setPid(metsXml.pid());
        dc.setModified(DateTimeConverter.timestampWithTimezone(metsXml.lastModDate()));
        dc.setPrefix("dc");
        dc.setData(result);
        dc.setOaiId("");
        dc.setSets(getSetSpecs("dc", result));

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
    private List<String> getSetSpecs(String format, Document dissemination) throws XPathExpressionException {
        List<String> setSpecs = new ArrayList<>();

        for (SetsConfig.Set setObj : sets.getSetObjects()) {
            String predicateKey = null;
            String predicateValue = null;

            if (setObj.getPredicate() != null && !setObj.getPredicate().isEmpty()) {

                if (setObj.getPredicate().contains("=")) {
                    String[] predicate = setObj.getPredicate().split("=");
                    predicateKey = predicate[0];
                    predicateValue = predicate[1];

                    if (!predicateValue.contains("/")) {

                        if (matchTerm(predicateKey, predicateValue, format, dissemination)) {
                            setSpecs.add(setObj.getSetSpec());
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

        return setSpecs;
    }

    private boolean matchTerm(String key, String value, String format, Document dissemination) throws XPathExpressionException {
        DissTerms.Term term = dt.getTerm(key, format);
        XPath xPath = DocumentXmlUtils.xpath(dt.getMapXmlNamespaces());
        Node node = null;

        if (term != null) {

            if (!term.getTerm().isEmpty()) {
                node = (Node) xPath.compile(term.getTerm().replace("$val", value)).evaluate(dissemination, XPathConstants.NODE);
            }
        }

        return (node != null) ? true : false;
    }
}
