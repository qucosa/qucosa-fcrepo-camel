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
import org.apache.xerces.dom.ElementNSImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
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

public class XMetaDissTransformer extends AbstractDisseminationTransform implements Expression {

    @Override
    public <T> T evaluate(Exchange exchange, Class<T> aClass) {
        ElementNSImpl elem = (ElementNSImpl) exchange.getIn().getBody();
        Document metsDoc = elem.getOwnerDocument();
        StreamSource xslSource = new StreamSource(this.getClass().getResourceAsStream(exchange.getProperty("xsltStylesheetResourceName").toString()));

        try {
            exchange.getIn().setBody(transformXmetadisDocument(exchange, metsDoc, xslSource));
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return (T) exchange.getIn().getBody();
    }

    private Document transformXmetadisDocument(Exchange exchange, Document metsDoc, StreamSource xslSource) throws XPathExpressionException, TransformerException, UnsupportedEncodingException {
        Transformer transformer = null;
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);

        Map<String, String> values = new LinkedHashMap<String, String>() {
            {
                put("AGENT", extractAgent(metsDoc));
            }

            {
                put("PID", extractPid(true, metsDoc));
            }
        };

        StringSubstitutor substitutor = new StringSubstitutor(values, "##", "##");
        String transferUrl = substitutor.replace(exchange.getProperty("transfer.url.pattern").toString());

        transformer = TransformerFactory.newInstance().newTransformer(xslSource);
        transformer.setParameter("transfer_url", transferUrl);
        transformer.transform(new DOMSource(metsDoc), streamResult);

        return DocumentXmlUtils.document(new ByteArrayInputStream(stringWriter.toString().getBytes("UTF-8")), true);
    }

}
