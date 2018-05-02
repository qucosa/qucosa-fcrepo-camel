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

import de.qucosa.fcrepo3.component.mapper.MetsXmlMapper;
import de.qucosa.utils.DocumentXmlUtils;
import de.qucosa.utils.SimpleNamespaceContext;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.commons.text.StringSubstitutor;
import org.apache.xerces.dom.ElementNSImpl;
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

public class DcTransformer extends AbstractDisseminationTransform implements Expression {

    @Override
    public <T> T evaluate(Exchange exchange, Class<T> aClass) {
        ElementNSImpl elem = (ElementNSImpl) exchange.getIn().getBody();
        Document metsDoc = elem.getOwnerDocument();
//        Document metsDoc = (Document) exchange.getIn().getBody();
        MetsXmlMapper metsXml = new MetsXmlMapper(metsDoc, dissTerms.getMapXmlNamespaces());
        StreamSource xslSource = new StreamSource(this.getClass().getResourceAsStream(exchange.getProperty("xsltStylesheetResourceName").toString()));

        try {
//            RecordTransport record = dcRecord(extractPid(true, metsDoc),
//                    DateTimeConverter.timestampWithTimezone(metsXml.lastModDate()),
//                    transformDcDocument(exchange, metsDoc, xslSource));

            exchange.getIn().setBody(transformDcDocument(exchange, metsDoc, xslSource));
        } catch (XPathExpressionException e) {

        } catch (TransformerException e) {

        } catch (UnsupportedEncodingException e) {

        }

        return (T) exchange.getIn().getBody();
    }

    private Document transformDcDocument(Exchange exchange, Document metsDoc, StreamSource xslSource) throws XPathExpressionException, TransformerException, UnsupportedEncodingException {
        Document transDoc = null;
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);

        Map<String, String> values = new LinkedHashMap<String, String>() {
            {
                put("AGENT", extractAgent(metsDoc));
            }

            {
                put("PID", extractPid(Boolean.valueOf(exchange.getProperty("transferUrlPidencode").toString()), metsDoc));
            }
        };

        StringSubstitutor substitutor = new StringSubstitutor(values, "##", "##");
        String transferUrl = substitutor.replace(exchange.getProperty("transfer.url.pattern").toString());

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(xslSource);
        transformer.setParameter("transfer_url", transferUrl);
        transformer.transform(new DOMSource(metsDoc), streamResult);

        return DocumentXmlUtils.document(new ByteArrayInputStream(stringWriter.toString().getBytes("UTF-8")), true);
    }

//    private RecordTransport dcRecord(String pid, Timestamp lastModDate, Document result) throws XPathExpressionException {
//        RecordTransport record = new RecordTransport();
//        record.setPid(pid);
//        record.setModified(lastModDate);
//        record.setPrefix("dc");
//        record.setData(result);
//        record.setSets(getSetSpecs("dc", result));
//        record.setOaiId("");
//        return record;
//    }

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
