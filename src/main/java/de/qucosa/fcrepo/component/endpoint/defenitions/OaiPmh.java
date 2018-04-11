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

package de.qucosa.fcrepo.component.endpoint.defenitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.fcrepo.component.EndpointDefAbstract;
import de.qucosa.fcrepo.component.EndpointDefInterface;
import de.qucosa.fcrepo.component.FedoraEndpoint;
import de.qucosa.fcrepo.component.xml.utils.DocumentXmlUtils;
import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultProducer;
import org.apache.http.impl.client.CloseableHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OaiPmh extends EndpointDefAbstract implements EndpointDefInterface {
    final Set<String> identifiers = new HashSet<>();

    @Override
    public Consumer getConsumer() {
        return new DefaultConsumer(endpoint, processor) {
            private CloseableHttpClient fedoraClient;

            @Override
            protected void doStart() throws Exception {
                super.doStart();
                fedoraClient = endpoint.fedoraClient();
                buildObjects(null);
                Exchange exchange = endpoint.createExchange();

                if (fedoraClient != null) {
                    exchange.getIn().setBody(identifiers);
                } else {
                    exchange.getIn().setBody(null);
                }

                fedoraClient.close();
                processor.process(exchange);
            }
        };
    }

    public void callIdents() {
    }

    @Override
    public Producer getProducer() {
        return new DefaultProducer(endpoint) {

            @SuppressWarnings("unused")
            @Override
            public void process(Exchange exchange) throws Exception {
                ObjectMapper om = new ObjectMapper();
//                System.out.println(exchange.getIn().getBody(String.class));

//                URL url = new URL("http://localhost:8080/qucosa-oai-provider/identifieres/add");
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setDoOutput(true);
//                connection.setRequestMethod("POST");
//                connection.setRequestProperty("Content-Type", "application/json");
//                
//                OutputStream outputStream = connection.getOutputStream();
//                outputStream.write(json.getBytes());
//                outputStream.flush();
//                
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String output = null;
//                
//                while ((output = reader.readLine()) != null) {
//                    System.out.println(output);
//                }
//                
//                connection.disconnect();
            }
        };
    }

    private String xml(String resumptionToken) {
        return (resumptionToken == null)
                ? endpoint.loadFromFedora(FedoraEndpoint.OAIPMH_LISTIDENTIFIERS_URL_WITHOUT_RESUMPTIONTOKEN, endpoint.getShema(), endpoint.getHost(), endpoint.getPort())
                : endpoint.loadFromFedora(FedoraEndpoint.OAIPMH_LISTIDENTIFIERS_URL_WITH_RESUMPTIONTOKEN, endpoint.getShema(), endpoint.getHost(), endpoint.getPort(), resumptionToken);
    }

    private void buildObjects(String resumptionToken) {
        String xml = xml(resumptionToken);

        try {
            Document document = DocumentXmlUtils.document(new ByteArrayInputStream(xml.getBytes("UTF-8")), false);
            XPath xPath = DocumentXmlUtils.xpath(endpoint.getConfiguration().getDissConf().getMapXmlNamespaces());
            Node rst = document.getElementsByTagName("resumptionToken").item(0);
            NodeList headers = (NodeList) xPath.compile("//header").evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < headers.getLength(); i++) {
                Node header = headers.item(i);
                header.getChildNodes();
                String identifire = (String) xPath.compile("./identifier/text()").evaluate(header, XPathConstants.STRING);
                Pattern idPattern = Pattern.compile("qucosa:\\d+");
                Matcher idMatch = idPattern.matcher(identifire);

                if (idMatch.find()) {
                    identifiers.add(idMatch.group(0));
                }
            }

            if (rst != null) {
                buildObjects(rst.getTextContent());
            }
        } catch (UnsupportedEncodingException | XPathExpressionException e) {
            e.printStackTrace();
        }
    }
}
