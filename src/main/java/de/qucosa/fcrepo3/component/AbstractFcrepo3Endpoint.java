package de.qucosa.fcrepo3.component;

import java.io.IOException;
import java.nio.charset.Charset;

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

abstract public class AbstractFcrepo3Endpoint extends DefaultEndpoint {
    protected static final String OAIPMH_LISTIDENTIFIERS_URL_WITHOUT_RESUMPTIONTOKEN = "%s://%s:%s/fedora/oai?verb=ListIdentifiers&metadataPrefix=oai_dc";
    
    protected static final String OAIPMH_LISTIDENTIFIERS_URL_WITH_RESUMPTIONTOKEN = "%s://%s:%s/fedora/oai?verb=ListIdentifiers&resumptionToken=%s";
    
    protected static final String OAIPMH_LISTRECORDS_URL_WITHOUT_RESUMPTIONTOKEN = "%s://%s:%s/fedora/oai?verb=ListRecords&metadataPrefix=oai_dc";
    
    protected static final String OAIPMH_LISTRECORDS_URL_WITH_RESUMPTIONTOKEN = "%s://%s:%s/fedora/oai?verb=ListRecords&resumptionToken=%s";
    
    protected static final String METS_URL = "%s://%s:%s/mets?pid=%s";
    
    private Logger logger = LoggerFactory.getLogger(FedoraEndpoint.class);
    
    @UriParam
    private Fcrepo3Configuration configuration;
    @UriParam
    private String schema = "http";
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
    
    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
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
