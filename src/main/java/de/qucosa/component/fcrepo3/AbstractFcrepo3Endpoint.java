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

package de.qucosa.component.fcrepo3;

import org.apache.camel.Component;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractFcrepo3Endpoint extends DefaultEndpoint {
    private Logger logger = LoggerFactory.getLogger(AbstractFcrepo3Endpoint.class);

    @UriParam
    private Fcrepo3Configuration configuration;
    @UriParam
    private String fedoraHosturl;
    @UriParam
    private String fedoraCredentials;

    public AbstractFcrepo3Endpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    public String getFedoraHosturl() { return fedoraHosturl; }

    public void setFedoraHosturl(String fedoraHosturl) { this.fedoraHosturl = fedoraHosturl; }

    public String getFedoraCredentials() { return fedoraCredentials; }

    public void setFedoraCredentials(String fedoraCredentials) { this.fedoraCredentials = fedoraCredentials; }

    public Fcrepo3Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Fcrepo3Configuration configuration) { this.configuration = configuration; }
}
