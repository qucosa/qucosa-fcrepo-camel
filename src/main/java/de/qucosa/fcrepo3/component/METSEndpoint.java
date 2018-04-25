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

package de.qucosa.fcrepo3.component;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.spi.UriParam;
import org.apache.http.impl.client.CloseableHttpClient;
import org.w3c.dom.NodeList;

public class METSEndpoint extends AbstractFcrepo3Endpoint {
    @UriParam
    private String supplement;
    
    public METSEndpoint(String endpointUri, Component component, Fcrepo3Configuration configuration) {
        super(endpointUri, component);
        setConfiguration(configuration);
    }
    
    @Override
    public Consumer createConsumer(Processor arg0) {
        return null;
    }

    @Override
    public Producer createProducer() {
        return new DefaultProducer(this) {

            @Override
            public void process(Exchange exchange) throws Exception {
                CloseableHttpClient fedoraClient = fedoraClient();
                NodeList list = (NodeList) exchange.getIn().getBody();
                String pid = list.item(0).getNodeValue();
                String metsXml = loadFromFedora(AbstractFcrepo3Endpoint.METS_URL, getSchema(), getHost(), getPort(), pid);
                exchange.getIn().setBody(metsXml);
                fedoraClient.close();
            }
        };
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
    
    public String getSupplement() {
        return supplement;
    }

    public void setSupplement(String supplement) {
        this.supplement = supplement;
    }
}
