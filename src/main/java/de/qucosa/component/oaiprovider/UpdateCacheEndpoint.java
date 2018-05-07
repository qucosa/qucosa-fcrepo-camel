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

package de.qucosa.component.oaiprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.component.oaiprovider.model.RecordTransport;
import org.apache.camel.*;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultProducer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Set;

public class UpdateCacheEndpoint extends DefaultEndpoint {
    private Endpoint endpoint;

    public UpdateCacheEndpoint(String endpointUri, Component component, OaiProviderConfiguration configuration) {
        super(endpointUri, component);
        endpoint = this;
    }

    @Override
    public Consumer createConsumer(Processor processor) {
        return null;
    }

    @Override
    public Producer createProducer() {
        return new DefaultProducer(endpoint) {

            @Override
            public void process(Exchange exchange) throws IOException {
                System.out.println(exchange.getIn().getBody());
                ObjectMapper om = new ObjectMapper();
                Set<RecordTransport> records = (Set<RecordTransport>) exchange.getIn().getBody();
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost post = new HttpPost("http://localhost:8080/qucosa-oai-provider/record/update");
                StringEntity stringEntity = new StringEntity(om.writeValueAsString(records));
                post.addHeader("content-type", "application/json");
                post.setEntity(stringEntity);
                HttpResponse httpResponse = httpClient.execute(post);
                ((CloseableHttpClient) httpClient).close();
            }
        };
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
