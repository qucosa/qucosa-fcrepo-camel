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

import de.qucosa.fcrepo.component.EndpointDefAbstract;
import de.qucosa.fcrepo.component.EndpointDefInterface;
import de.qucosa.fcrepo.component.FedoraEndpoint;
import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultProducer;
import org.apache.http.impl.client.CloseableHttpClient;

public class IdentifireQue extends EndpointDefAbstract implements EndpointDefInterface {
    @Override
    public Consumer getConsumer() {
        return null;
    }

    @Override
    public Producer getProducer() {
        return new DefaultProducer(endpoint) {
            @Override
            public void process(Exchange exchange) throws Exception {
                CloseableHttpClient fedoraClient = endpoint.fedoraClient();
                String pid = exchange.getIn().getBody().toString();
                String metsXml = endpoint.loadFromFedora(FedoraEndpoint.METS_URL, endpoint.getShema(), endpoint.getHost(), endpoint.getPort(), pid);
                exchange.getIn().setBody(metsXml);
                fedoraClient.close();
            }
        };
    }

}
