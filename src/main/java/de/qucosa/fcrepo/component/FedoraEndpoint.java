package de.qucosa.fcrepo.component;

import java.lang.reflect.Method;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;

import de.qucosa.fcrepo.fedora.api.services.FedoraOaiService;

@UriEndpoint(scheme = "fcrepo", syntax = "fcrepo:fedora:endpointDef", title = "Fedora Endpoint")
public class FedoraEndpoint extends DefaultEndpoint {
	@UriParam
	private FcrepoConfiguration configuration;
	
	private FedoraOaiService objectService;

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

    public FedoraOaiService getObjectService() {
        return objectService;
    }
    
	@Override
	protected void doStart() throws Exception {
		super.doStart();
	}

	@Override
	protected void doStop() throws Exception {
	    super.doStop();
	}
    
    @SuppressWarnings("rawtypes")
    private EndpointDefInterface endpointDef() throws Exception {
        Class clazz = Class.forName("de.qucosa.fcrepo.component.endpoint.defenitions." + getConfiguration().getEndpointDef());
        return (EndpointDefInterface) clazz.newInstance();
    }
}
