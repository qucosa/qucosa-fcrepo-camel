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

import de.qucosa.utils.DocumentXmlUtils;
import de.qucosa.utils.SimpleNamespaceContext;
import org.apache.commons.text.StringSubstitutor;
import org.w3c.dom.Document;

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
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DcDissTransformer {
    private final StreamSource xslSource;

    private Document metsDoc = null;

    private String transferUrlPattern;

    private boolean transferUrlPidencode;

    private Map<String, String> agentNameSubstitutions;

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

        StringSubstitutor substitutor = new StringSubstitutor(values, "##", "##");
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
