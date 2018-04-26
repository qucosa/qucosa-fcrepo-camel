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
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

abstract public class AbstractFcrepo3Endpoint extends DefaultEndpoint {
    protected static final String OAIPMH_LISTIDENTIFIERS_URL_WITHOUT_RESUMPTIONTOKEN = "%s://%s:%s/fedora/oai?verb=ListIdentifiers&metadataPrefix=oai_dc";

    protected static final String OAIPMH_LISTIDENTIFIERS_URL_WITH_RESUMPTIONTOKEN = "%s://%s:%s/fedora/oai?verb=ListIdentifiers&resumptionToken=%s";

    protected static final String OAIPMH_LISTRECORDS_URL_WITHOUT_RESUMPTIONTOKEN = "%s://%s:%s/fedora/oai?verb=ListRecords&metadataPrefix=oai_dc";

    protected static final String OAIPMH_LISTRECORDS_URL_WITH_RESUMPTIONTOKEN = "%s://%s:%s/fedora/oai?verb=ListRecords&resumptionToken=%s";

    protected static final String METS_URL = "%s://%s:%s/mets?pid=%s";

    private Logger logger = LoggerFactory.getLogger(AbstractFcrepo3Endpoint.class);

    @UriParam
    private Fcrepo3Configuration configuration;
    @UriParam
    private String fedoraHosturl;
    @UriParam
    private String user = "fedoraAdmin";
    @UriParam
    private String password = "fedoraAdmin";
    @UriParam
    private String verb;
    @UriParam
    private String set;
    @UriParam
    private String metadataPrefix;
    @UriParam
    private String from;
    @UriParam
    private String until;
    @UriParam
    private String source;

    private CloseableHttpClient httpClient = null;

    public AbstractFcrepo3Endpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    public CloseableHttpClient fedoraClient() {

        if (getUser() != null && !getUser().isEmpty() && getPassword() != null && !getPassword().isEmpty()) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(getUser(), getPassword()));

            httpClient = HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager())
                    .setDefaultCredentialsProvider(credentialsProvider).build();
        } else {
            httpClient = HttpClientBuilder.create().build();
        }

        return httpClient;
    }

    public String loadFromFedora(String uriPattern, Object... params) {
        HttpResponse response = null;
        String content = "";

        try {
            response = httpClient.execute(new HttpGet(String.format(uriPattern, params)));

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = IOUtils.toString(response.getEntity().getContent(), Charset.forName("UTF-8"));
            }

        } catch (IOException e) {
            logger.debug("Cannot load XML Data from fedora repositroy. Check your params!");
            logger.debug(String.format(uriPattern, params));
        } finally {
            consumeResponseEntity(response);
        }

        return content;
    }

    public String getFedoraHosturl() {
        return fedoraHosturl;
    }

    public void setFedoraHosturl(String fedoraHosturl) {
        this.fedoraHosturl = fedoraHosturl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public void setMetadataPrefix(String metadataPrefix) {
        this.metadataPrefix = metadataPrefix;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getUntil() {
        return until;
    }

    public void setUntil(String until) {
        this.until = until;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Fcrepo3Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Fcrepo3Configuration configuration) {
        this.configuration = configuration;
    }

    private void consumeResponseEntity(HttpResponse response) {
        try {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        } catch (IOException ignored) {
        }
    }
}
