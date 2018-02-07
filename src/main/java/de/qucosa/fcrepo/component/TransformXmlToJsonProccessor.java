package de.qucosa.fcrepo.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import de.qucosa.fcrepo.fedora.api.mappings.xml.Set;

public class TransformXmlToJsonProccessor implements Processor {
	
	@SuppressWarnings("unchecked")
    @Override
	public void process(Exchange exchange) throws Exception {
		System.out.println("Process is started");
		List<Set> sets = exchange.getIn().getBody(List.class);
		exchange.getIn().setBody(sets, ArrayList.class);
	}
}
