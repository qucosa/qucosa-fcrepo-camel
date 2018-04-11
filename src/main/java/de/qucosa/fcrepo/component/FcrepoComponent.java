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

package de.qucosa.fcrepo.component;

import de.qucosa.fcrepo.component.mapper.DissTerms;
import de.qucosa.fcrepo.component.mapper.SetsConfig;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

public class FcrepoComponent extends DefaultComponent {

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        FcrepoConfiguration configuration = new FcrepoConfiguration();
        setProperties(configuration, parameters);

        if (remaining.contains(":")) {
            String[] remainingDef = remaining.split(":");
            configuration.setEndpointDef(remainingDef[1]);
            DissTerms dt = new DissTerms();
            SetsConfig sets = new SetsConfig();
            configuration.setDissConf(dt);
            configuration.setSets(sets);

            if (remainingDef[0].endsWith("fedora")) {
                Endpoint endpoint = new FedoraEndpoint(uri, this, configuration);
                return endpoint;
            }

            throw new Exception("Unknown endpoint URI:" + remainingDef[0]);
        } else {

            if (remaining.endsWith("fedora")) {
                Endpoint endpoint = new FedoraEndpoint(uri, this, configuration);
                return endpoint;
            }
        }

        throw new Exception("Unknown endpoint URI:" + remaining);
    }
}
