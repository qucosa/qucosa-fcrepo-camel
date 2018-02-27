package de.qucosa.fcrepo.component.endpoint.defenitions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultProducer;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.fcrepo.component.EndpointDefAbstract;
import de.qucosa.fcrepo.component.EndpointDefAnnotation;
import de.qucosa.fcrepo.component.EndpointDefInterface;

@EndpointDefAnnotation(isConsumer = true, isProducer = true)
public class SetSpec<T> extends EndpointDefAbstract implements EndpointDefInterface {

    @Override
    public Consumer getConsumer() {
        return new DefaultConsumer(endpoint, processor) {
            @Override
            protected void doStart() throws Exception {
                super.doStart();
                ObjectMapper om = new ObjectMapper();
                File setSpecs = new File("/home/dseelig/opt/oaiprovider/config/list-set-conf.json");
                Set<de.qucosa.fcrepo.component.pojos.oaiprivider.Set> json = null;
                try {
                    json = om.readValue(setSpecs, om.getTypeFactory().constructCollectionType(Set.class, de.qucosa.fcrepo.component.pojos.oaiprivider.Set.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Exchange exchange = endpoint.createExchange();
                exchange.getIn().setBody(om.writeValueAsString(json));
                processor.process(exchange);
            }
            
            @Override
            protected void doStop() throws Exception {
                super.doStop();
            }
        };
    }

    @Override
    public Producer getProducer() {
        return new DefaultProducer(endpoint) {
            @Override
            public void process(Exchange exchange) throws Exception {
                URL url = new URL("http://localhost:8080/qucosa-oai-provider/sets/add");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(exchange.getIn().getBody().toString().getBytes());
                outputStream.flush();
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String output = null;
                
                while ((output = reader.readLine()) != null) {
                    System.out.println(output);
                }
                
                connection.disconnect();
            }
            
            @Override
            protected void doStop() throws Exception {
                super.doStop();
            }
        };
    }

}
