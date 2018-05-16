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

import de.qucosa.model.DissTerms;
import de.qucosa.utils.DocumentXmlUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultScheduledPollConsumer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.ByteArrayInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdentifiersPollConsumer extends DefaultScheduledPollConsumer {

    private Processor processor;

    private OaiPmhEndpoint endpoint;

    private String resToken = null;

    int cntPoll;

    private static final Pattern PATTERN = Pattern.compile("qucosa:\\d+$");

    public IdentifiersPollConsumer(OaiPmhEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.processor = processor;
        this.endpoint = endpoint;
    }

    @Override
    protected int poll() throws Exception {
        cntPoll++;
        Exchange exchange = endpoint.createExchange();
        DissTerms dissTerms = (DissTerms) exchange.getContext().getRegistry().lookupByName("dissTerms");
        String xml;

        if (resToken == null) {
            xml = endpoint.xml(endpoint.getUrl() + "?verb=" + endpoint.getVerb() + "&metadataPrefix=" + endpoint.getMetadataPrefix());
        } else {
            xml = endpoint.xml(endpoint.getUrl() + "?verb=" + endpoint.getVerb() + "&resumptionToken=" + resToken);
        }

        if (!xml.isEmpty()) {
            Document document = DocumentXmlUtils.document(new ByteArrayInputStream(xml.getBytes("UTF-8")), false);
            XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
            String token = (String) xPath.compile("//ListIdentifiers/resumptionToken/text()").evaluate(document, XPathConstants.STRING);
            resToken = (token != null && !token.isEmpty()) ? token : null;
            NodeList nodeList = (NodeList) xPath.compile("//ListIdentifiers/header/identifier/text()").evaluate(document, XPathConstants.NODESET);

            if (nodeList != null && nodeList.getLength() > 0) {

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    Matcher matcher = PATTERN.matcher(node.getNodeValue());

                    if (matcher.find()) {
                        Exchange send = endpoint.createExchange();
                        send.getIn().setBody(matcher.group());
                        processor.process(send);
                    }
                }
            }
        }

        return cntPoll;
    }
}
