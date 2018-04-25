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

import de.qucosa.oaiprovider.component.model.DissTerms;
import de.qucosa.oaiprovider.component.model.SetsConfig;
import org.apache.camel.Component;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

public class Fcrepo3Component extends DefaultComponent {

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Fcrepo3Configuration configuration = new Fcrepo3Configuration();
        setProperties(configuration, parameters);
        Endpoint endpoint = null;

        if (remaining != null && !remaining.isEmpty()) {
            DissTerms dt = new DissTerms();
            SetsConfig sets = new SetsConfig();
            configuration.setDissConf(dt);
            configuration.setSets(sets);

            switch (remaining.toLowerCase()) {
                case "mets":
                    endpoint = mets(uri, this, configuration);
                    break;
            }

            return endpoint;
        }

        throw new Exception("Unknown endpoint URI:" + remaining);
    }

    private Endpoint mets(String uri, Component component, Fcrepo3Configuration configuration) {
        return new METSEndpoint(uri, component, configuration);
    }
}
