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

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.lang.reflect.Method;
import java.util.Map;

public class OaiProviderComponent extends DefaultComponent {
    private String uri;

    private OaiProviderConfiguration configuration = null;

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        this.uri = uri;
        configuration = new OaiProviderConfiguration();
        setProperties(configuration, parameters);

        Method method = getClass().getDeclaredMethod(remaining);
//        return (Endpoint) method.invoke(this);

        switch (remaining.toLowerCase()) {
            case "update":
                return update();
            default:
                throw new Exception();
        }
    }

    /**
     * Execute via java reflection api in createEndpoint method. This endpoint
     * updated the oai provider cache.
     *
     * @return
     */
    @SuppressWarnings("unused")
    private Endpoint update() {
        UpdateCacheEndpoint endpoint = new UpdateCacheEndpoint(uri, this, configuration);
        return endpoint;
    }
}
