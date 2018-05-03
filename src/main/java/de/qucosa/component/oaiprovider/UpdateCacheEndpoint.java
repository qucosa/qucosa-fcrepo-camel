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
import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultProducer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
                URL url = new URL("http://localhost:8080/qucosa-oai-provider/record/update");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(om.writeValueAsBytes(records));
                outputStream.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String output = null;

                while ((output = reader.readLine()) != null) {
                    System.out.println(output);
                }

                connection.disconnect();
            }
        };
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
