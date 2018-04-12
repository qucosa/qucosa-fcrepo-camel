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

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@UriEndpoint(scheme = "fcrepo", syntax = "fcrepo:fedora:endpointDef", title = "Fedora Endpoint")
public class FedoraEndpoint extends DefaultEndpoint {
    public static final String OAIPMH_LISTIDENTIFIERS_URL_WITHOUT_RESUMPTIONTOKEN = "%s://%s:%s/fedora/oai?verb=ListIdentifiers&metadataPrefix=oai_dc";
    public static final String OAIPMH_LISTIDENTIFIERS_URL_WITH_RESUMPTIONTOKEN = "%s://%s:%s/fedora/oai?verb=ListIdentifiers&resumptionToken=%s";
    public static final String OAIPMH_LISTRECORDS_URL_WITHOUT_RESUMPTIONTOKEN = "%s://%s:%s/fedora/oai?verb=ListRecords&metadataPrefix=oai_dc";
    public static final String OAIPMH_LISTRECORDS_URL_WITH_RESUMPTIONTOKEN = "%s://%s:%s/fedora/oai?verb=ListRecords&resumptionToken=%s";
    public static final String METS_URL = "%s://%s:%s/mets?pid=%s";
    private Logger logger = LoggerFactory.getLogger(FedoraEndpoint.class);
    @UriParam
    private FcrepoConfiguration configuration;
    @UriParam
    private String shema = "http";
    @UriParam
    private String port = "8080";
    @UriParam
    private String host = "localhost";
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
    private Set<String> identifires = new HashSet<>();

    public FedoraEndpoint(String endpointUri, Component component, FcrepoConfiguration configuration) {
        super(endpointUri, component);
        this.configuration = configuration;
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        try {
            Method method = endpointDef().getClass().getMethod("getConsumer");
            EndpointDefInterface endpointDef = endpointDef();
            endpointDef.setEndpoint(this);
            endpointDef.setProcessor(processor);
            return (method.getReturnType() != null) ? endpointDef.getConsumer() : null;
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }

        throw new Exception("Unsupported consumer in endpoint definition " + endpointDef().getClass().getCanonicalName());
    }

    @Override
    public Producer createProducer() throws Exception {
        Method method = endpointDef().getClass().getMethod("getProducer");

        try {
            EndpointDefInterface endpointDef = endpointDef();
            endpointDef.setEndpoint(this);
            return (method.getReturnType() != null) ? endpointDef.getProducer() : null;
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }

        throw new Exception("Unsupported producer in endpoint definition " + endpointDef().getClass().getCanonicalName());
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public CloseableHttpClient fedoraClient() {

        if (getUser() != null && !getUser().isEmpty() && getPassword() != null && !getPassword().isEmpty()) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(getUser(), getPassword()));

//            if (isHttpConnectionOpen()) {
            httpClient = HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager())
                    .setDefaultCredentialsProvider(credentialsProvider).build();
//            }
        } else {

//            if (isHttpConnectionOpen()) {
            httpClient = HttpClientBuilder.create().build();
//            }
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

    public FcrepoConfiguration getConfiguration() {
        return configuration;
    }

    public String getShema() {
        return shema;
    }

    public void setShema(String shema) {
        this.shema = shema;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public Set<String> getIdentifires() {
        return identifires;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }

    @SuppressWarnings({"resource", "unused"})
    private boolean isHttpConnectionOpen() {
        boolean isOpen = false;
        HttpClientConnectionManager hccm = new BasicHttpClientConnectionManager();
        HttpRoute hr = new HttpRoute(new HttpHost(getHost(), Integer.valueOf(getPort())));

        try {
            ConnectionRequest cr = hccm.requestConnection(hr, null);
            HttpClientConnection conn = cr.get(10, TimeUnit.SECONDS);

            if (conn.isOpen()) {
                isOpen = true;
            }
        } catch (ConnectionPoolTimeoutException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return isOpen;
    }

    @SuppressWarnings("rawtypes")
    private EndpointDefInterface endpointDef() throws Exception {
        Class clazz = Class.forName("de.qucosa.fcrepo.component.endpoint.definitions." + getConfiguration().getEndpointDef());
        return (EndpointDefInterface) clazz.newInstance();
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
