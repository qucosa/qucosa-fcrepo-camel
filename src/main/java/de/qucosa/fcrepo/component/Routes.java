package de.qucosa.fcrepo.component;

import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.context.annotation.Bean;

import de.qucosa.fcrepo.component.endpoint.predicates.IdentifireQueuePredicate;
import de.qucosa.fcrepo.component.endpoint.strategies.IdentifireAggregateStrategy;;

public class Routes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        
        from("direct:aggregateIdents")
            .id("franz")
            .resequence().body().batch().timeout(500L)
            .log("${body}")
            .to("mock:test");
        
        // (from) load all exists identifieres from the fedora repo
        // (to) write identifieres in the database
        
        
        /*
        from("timer:tick?period=700")
            .setBody(constant("qucosa:1234"))
            .to("direct:aggregateIdents");

        from("timer:tock?period=500")
        .setBody(constant("qucosa:5678"))
        .to("direct:aggregateIdents");
*/
        
        
        from("fcrepo:fedora:OaiPmh?shema=http&host=sdvcmr-app03.slub-dresden.de&port=8080")
            .id("hanne")
            .split().body()
            .to("direct:aggregateIdents");
        
        from("activemq:topic:fedora.apim.*")
            .id("lina")
            .setBody(xpath("//summary[@type=\"text\"]/text()"))
            .to("direct:aggregateIdents");
      
        
        
        
        //from("direct:test").to("mock:out");
        
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
