package de.qucosa.fcrepo.component.endpoint.defenitions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultProducer;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.fcrepo.component.EndpointDefAbstract;
import de.qucosa.fcrepo.component.EndpointDefInterface;
import de.qucosa.fcrepo.component.mapper.DissTerms.DissFormat;
import de.qucosa.fcrepo.component.pojos.oaiprivider.Format;

public class QucosaObjects extends EndpointDefAbstract implements EndpointDefInterface {

    @Override
    public Consumer getConsumer() {
        return new DefaultConsumer(endpoint, processor) {
            @Override
            protected void doStart() throws Exception {
                super.doStart();
                ObjectMapper om = new ObjectMapper();
                Set<DissFormat> dissFormats = endpoint.getConfiguration().getDissConf().formats();
                Set<Format> formats = new HashSet<>();
                
                for (DissFormat df : dissFormats) {
                    Format fm = new Format();
                    fm.setMdprefix(df.getFormat());
                    fm.setDissType(df.getDissType());
                    fm.setLastpolldate(new Timestamp(new Date().getTime()));
                    formats.add(fm);
                }
                
                Exchange exchange = endpoint.createExchange();
                exchange.setProperty("fedora", endpoint);
                exchange.getIn().setBody(om.writeValueAsString(formats));
                processor.process(exchange);
            }
        };
    }

    @Override
    public Producer getProducer() {
        return new DefaultProducer(endpoint) {
            
            @Override
            public void process(Exchange exchange) throws Exception {
               String json = exchange.getIn().getBody().toString();
               URL url = new URL("http://localhost:8080/qucosa-oai-provider/formats/add");
               HttpURLConnection connection = (HttpURLConnection) url.openConnection();
               connection.setDoOutput(true);
               connection.setRequestMethod("POST");
               connection.setRequestProperty("Content-Type", "application/json");
               
               OutputStream outputStream = connection.getOutputStream();
               outputStream.write(json.getBytes());
               outputStream.flush();
               
               BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
               String output = null;
               
               while ((output = reader.readLine()) != null) {
                   System.out.println(output);
               }
               
               connection.disconnect();
            }
        };
    }

}
