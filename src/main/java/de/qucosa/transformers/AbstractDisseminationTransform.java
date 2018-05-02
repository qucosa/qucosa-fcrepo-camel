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

package de.qucosa.transformers;

import de.qucosa.oaiprovider.component.model.DissTerms;
import de.qucosa.oaiprovider.component.model.SetsConfig;
import de.qucosa.utils.DocumentXmlUtils;
import de.qucosa.utils.SimpleNamespaceContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract public class AbstractDisseminationTransform {
    protected DissTerms dissTerms = new DissTerms();

    protected SetsConfig sets = new SetsConfig();

    protected String extractAgent(Document metsDoc) throws XPathExpressionException {
        String agent = null;
        XPath xPath = xpath();
        agent = (String) xPath.compile("//mets:agent[@ROLE='EDITOR' and @TYPE='ORGANIZATION']/mets:name[1]")
                .evaluate(metsDoc, XPathConstants.STRING);
        return agent;
    }

    protected String extractPid(boolean transferUrlPidencode, Document metsDoc) throws XPathExpressionException {
        String pid = null;

        if (transferUrlPidencode) {
            XPath xPath = xpath();
            pid = (String) xPath.compile("//mets:mets/@OBJID").evaluate(metsDoc, XPathConstants.STRING);
        }

        return pid;
    }

    protected XPath xpath() {
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(new SimpleNamespaceContext(new HashMap<String, String>() {
            {
                put("mets", "http://www.loc.gov/METS/");
            }
        }));
        return xPath;
    }

    protected List<String> getSetSpecs(String format, Document dissemination) throws XPathExpressionException {
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

    protected boolean matchTerm(String key, String value, String format, Document dissemination) throws XPathExpressionException {
        DissTerms.Term term = dissTerms.getTerm(key, format);
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
