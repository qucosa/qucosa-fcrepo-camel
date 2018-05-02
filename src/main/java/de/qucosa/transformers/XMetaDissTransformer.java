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
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.commons.text.StringSubstitutor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class XMetaDissTransformer extends AbstractDisseminationTransform implements Expression {
    private Document metsDoc = null;

    private String transferUrlPattern;

    private boolean transferUrlPidencode;

    private Map<String, String> agentNameSubstitutions;

    @Override
    public <T> T evaluate(Exchange exchange, Class<T> aClass) {
        return null;
    }

    public XMetaDissTransformer() { }

    public XMetaDissTransformer(String transferUrlPattern, String agentNameSubstitutions, boolean transferUrlPidencode) {
        this.transferUrlPattern = transferUrlPattern;
        this.transferUrlPidencode = transferUrlPidencode;
        this.agentNameSubstitutions = decodeSubstitutions(agentNameSubstitutions);
    }

    @SuppressWarnings("serial")
    public Document transformXmetaDissplus(Document metsDoc, StreamSource xslSource) throws TransformerFactoryConfigurationError, Exception, XPathExpressionException {
        this.metsDoc = metsDoc;
        Transformer transformer = null;
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        Document xmetadiss = null;

        Map<String, String> values = new LinkedHashMap<String, String>() {
            {
                put("AGENT", extractAgent(metsDoc));
            }

            {
                put("PID", extractPid(true, metsDoc));
            }
        };

        StringSubstitutor substitutor = new StringSubstitutor(values, "##", "##");
        String transferUrl = substitutor.replace(transferUrlPattern);

        transformer = TransformerFactory.newInstance().newTransformer(xslSource);
        transformer.setParameter("transfer_url", transferUrl);
        transformer.transform(new DOMSource(metsDoc), streamResult);

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        xmetadiss = documentBuilder.parse(new ByteArrayInputStream(stringWriter.toString().getBytes("UTF-8")));

        return xmetadiss;
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
}
