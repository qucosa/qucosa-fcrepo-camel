package de.qucosa.fcrepo.component;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class OaiProviderProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
//        System.out.println("OaiProviderProcessor: " + exchange.getIn().getBody().toString());
    }

}
