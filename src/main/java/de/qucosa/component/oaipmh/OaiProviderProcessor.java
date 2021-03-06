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

package de.qucosa.component.oaipmh;

import de.qucosa.component.oaipmh.model.RecordTransport;
import de.qucosa.config.DissTermsDao;
import de.qucosa.config.DissTermsMapper;
import de.qucosa.config.SetConfigDao;
import de.qucosa.config.SetConfigMapper;
import de.qucosa.utils.DateTimeConverter;
import de.qucosa.utils.DocumentXmlUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;

public class OaiProviderProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        DissTermsDao dissTerms = (DissTermsDao) exchange.getContext().getRegistry().lookupByName("dissTerms");
        SetConfigDao setsConfig = (SetConfigDao) exchange.getContext().getRegistry().lookupByName("setsConfig");
        Document dissemination = (Document) exchange.getIn().getBody();
        String format = exchange.getProperty("format").toString();

        RecordTransport record = new RecordTransport();
        record.setMdprefix(format);
        record.setPid(exchange.getProperty("pid").toString());
        record.setData(dissemination);
        record.setModified(DateTimeConverter.timestampWithTimezone(exchange.getProperty("lastmoddate").toString()));
        record.setSets(getSetSpecs(dissTerms, setsConfig, format, dissemination));
        record.setOaiId("");

        exchange.getIn().setBody(record);
    }

    private List<String> getSetSpecs(DissTermsDao dissTerms, SetConfigDao setsConfig, String format, Document dissemination) throws XPathExpressionException {
        List<String> setSpecs = new ArrayList<>();

        for (SetConfigMapper.Set setObj : setsConfig.getSetObjects()) {
            String predicateKey;
            String predicateValue;

            if (setObj.getPredicate() != null && !setObj.getPredicate().isEmpty()) {

                if (setObj.getPredicate().contains("=")) {
                    String[] predicate = setObj.getPredicate().split("=");
                    predicateKey = predicate[0];
                    predicateValue = predicate[1];

                    if (!predicateValue.contains("/")) {

                        if (matchTerm(predicateKey, predicateValue, format, dissTerms, dissemination)) {
                            setSpecs.add(setObj.getSetSpec());
                        }
                    } else {
                        String[] predicateValues = predicateValue.split("/");

                        if (predicateValues.length > 0) {

                            for (int i = 0; i < predicateValues.length; i++) {
                                String setspec = predicateKey + ":" + predicateValues[i];

                                if (matchTerm(predicateKey, predicateValues[i], format, dissTerms, dissemination)) {

                                    if (!setSpecs.contains(setspec)) {
                                        setSpecs.add(setspec);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    setSpecs.add(setObj.getPredicate());
                }
            }
        }

        return setSpecs;
    }

    private boolean matchTerm(String key, String value, String format, DissTermsDao dissTerms, Document dissemination) throws XPathExpressionException {
        DissTermsMapper.Term term = dissTerms.getTerm(key, format);
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        Node node = null;

        if (term != null) {

            if (!term.getTerm().isEmpty()) {
                node = (Node) xPath.compile(term.getTerm().replace("$val", value)).evaluate(dissemination, XPathConstants.NODE);
            }
        }

        return (node != null);
    }
}
