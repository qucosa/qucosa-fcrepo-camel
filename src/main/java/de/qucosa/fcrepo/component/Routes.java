package de.qucosa.fcrepo.component;

import java.io.ByteArrayInputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.context.annotation.Bean;
import org.w3c.dom.Document;

import de.qucosa.fcrepo.component.endpoint.predicates.IdentifireQueuePredicate;
import de.qucosa.fcrepo.component.endpoint.strategies.IdentifireAggregateStrategy;
import de.qucosa.fcrepo.component.mapper.DissTerms;
import de.qucosa.fcrepo.component.xml.utils.DocumentXmlUtils;
import de.qucosa.fcrepo.component.xml.utils.SimpleNamespaceContext;;

public class Routes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        final DissTerms dt = new DissTerms();
        
        from("direct:aggregateIdents")
            .id("franz")
            .resequence().body()
            .to("fcrepo:fedora:IdentifireQue?shema=http&host=192.168.42.28&port=8080")
            .filter().body().multicast()
            .to("fcrepo:fedora:OaiProvider", "fcrepo:fedora:ReportingDb", "fcrepo:fedora:QucosaElasticSearch")
            .end();
        
     // test dev host sdvcmr-app03.slub-dresden.de
        from("fcrepo:fedora:OaiPmh?shema=http&host=192.168.42.28&port=8080")
            .id("hanne")
            .split().body()
            .to("direct:aggregateIdents");
        
        from("activemq:topic:fedora.apim.*")
            .id("lina")
            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    String msg = (String) exchange.getIn().getBody();
                    Document document = DocumentXmlUtils.document(new ByteArrayInputStream(msg.getBytes("UTF-8")), false);
                    XPath xPath = DocumentXmlUtils.xpath();
                    xPath.setNamespaceContext(new SimpleNamespaceContext(dt.getMapXmlNamespaces()));
                    String id = (String) xPath.compile("//summary[@type=\"text\"]/text()").evaluate(document, XPathConstants.STRING);
                    exchange.getIn().setBody(id);
                }
            })
            .to("direct:aggregateIdents");  
        
        /*
        from("timer:tick?period=700")
            .setBody(constant("qucosa:1234"))
            .to("direct:aggregateIdents");

        from("timer:tock?period=500")
        .setBody(constant("qucosa:5678"))
        .to("direct:aggregateIdents");
*/
        
        
        // (from) load xml dissemination by format and pid from fedora repository and modified the xml data strings with record definitions
        // (to) save records in the database / save sets to records ids in the m:n table
//        from("fcrepo:fedora:LoadDatastream?shema=http&host=sdvcmr-app03.slub-dresden.de&port=8080")
//            .process(new MergeSetsRecordXmlProcessor())
//            .to("fcrepo:fedora:LoadDatastream").log("${body}");

        // from("fcrepo://fedora:OaiPmh?shema=http&host=localhost&port=4711&user=fedoraAdmin&password=fedoraAdmin")
        // .process(new TransformXmlToJsonProccessor())
        // .to("fcrepo://fedora:OaiPmh")
        // .to("mock:test");
        // .to("elasticsearch://elasticsearch?ip=192.168.42.27&port=9300&operation=INDEX&indexName=fedora&indexType=mods")
        // .log("${body}").stop();
    }
    
    @Bean
    private AggregationStrategy identifireAggregate() {
        return new IdentifireAggregateStrategy();
    }
    
    @Bean
    private Predicate identifireQueuePredicate () {
        return new IdentifireQueuePredicate();
    }
}
