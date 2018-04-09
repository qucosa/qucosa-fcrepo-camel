package de.qucosa.fcrepo.component;

import org.apache.camel.builder.RouteBuilder;

import de.qucosa.fcrepo.component.mapper.DissTerms;;

public class Routes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        final DissTerms dt = new DissTerms();
        
        from("direct:oaiprovider")
            .id("oaiProviderProcess")
            .autoStartup(false)
            .startupOrder(1)
            .process(new OaiProviderProcessor())
            .to("fcrepo:fedora:OaiProvider");
        
        from("direct:reportingDB")
            .id("reportingDBProcess")
            .autoStartup(false)
            .startupOrder(2)
            .process(new ReportingDbProcessor())
            .to("fcrepo:fedora:ReportingDb");
        
        // @todo replace the mock endpoint with elastic serach endpoint
        from("direct:qucosaelastic")
            .id("elasticSearchProcess")
            .autoStartup(false)
            .startupOrder(3)
            .process(new QucosaElasticSearchProcessor())
            .to("mock:test");
        
        from("direct:aggregateIdents")
            .id("cleanIdentifires")
            .autoStartup(false)
            .startupOrder(4)
            .resequence().body()
            .to("fcrepo:fedora:IdentifireQue?shema=http&host=192.168.42.28&port=8080")
            .filter().body().multicast()
            .to("direct:oaiprovider", "direct:reportingDB", "direct:qucosaelastic")
            .end();
        
     // test dev host sdvcmr-app03.slub-dresden.de
        from("fcrepo:fedora:OaiPmh?shema=http&host=192.168.42.28&port=8080")
            .id("fedoraOai")
            .startupOrder(5)
            .autoStartup(false)
            .split().body()
            .to("direct:aggregateIdents");
        
//        from("activemq:topic:fedora.apim.*")
//            .id("fedoraJms")
//            .startupOrder(3)
//            .autoStartup(false)
//            .process(new Processor() {
//                @Override
//                public void process(Exchange exchange) throws Exception {
//                    String msg = (String) exchange.getIn().getBody();
//                    Document document = DocumentXmlUtils.document(new ByteArrayInputStream(msg.getBytes("UTF-8")), false);
//                    XPath xPath = DocumentXmlUtils.xpath();
//                    xPath.setNamespaceContext(new SimpleNamespaceContext(dt.getMapXmlNamespaces()));
//                    String id = (String) xPath.compile("//summary[@type=\"text\"]/text()").evaluate(document, XPathConstants.STRING);
//                    exchange.getIn().setBody(id);
//                }
//            })
//            .to("direct:aggregateIdents");  
        
        
        // .to("elasticsearch://elasticsearch?ip=192.168.42.27&port=9300&operation=INDEX&indexName=fedora&indexType=mods")
    }
}
