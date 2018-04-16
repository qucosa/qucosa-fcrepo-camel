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

package de.qucosa.endpoint;

import de.qucosa.fcrepo.component.FedoraEndpoint;
import org.apache.camel.Processor;

public abstract class AbstractEndpointDefinition implements EndpointDefinition {
    protected FedoraEndpoint endpoint;

    protected Processor processor;

    @Override
    public void setEndpoint(FedoraEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void setProcessor(Processor processor) {
        this.processor = processor;
    }
}