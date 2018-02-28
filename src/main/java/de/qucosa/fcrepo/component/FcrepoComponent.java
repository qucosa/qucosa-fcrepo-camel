package de.qucosa.fcrepo.component;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import de.qucosa.fcrepo.component.mapper.DissTerms;

public class FcrepoComponent extends DefaultComponent {
	
	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		FcrepoConfiguration configuration = new FcrepoConfiguration();
		setProperties(configuration, parameters);
		
		if (remaining.contains(":")) {
		    String[] remainingDef = remaining.split(":");
		    configuration.setEndpointDef(remainingDef[1]);
		    DissTerms dt = new DissTerms();
		    configuration.setDissConf(dt);
		    
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
