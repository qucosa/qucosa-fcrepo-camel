package de.qucosa.oaiprovider.component;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

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
