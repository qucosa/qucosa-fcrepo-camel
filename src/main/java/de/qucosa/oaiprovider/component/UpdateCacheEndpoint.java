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

package de.qucosa.oaiprovider.component;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultProducer;

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
            public void process(Exchange exchange) {
                System.out.println(exchange.getIn().getBody());
            }
        };
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
