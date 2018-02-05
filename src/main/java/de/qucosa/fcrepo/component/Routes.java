package de.qucosa.fcrepo.component;

import org.apache.camel.builder.RouteBuilder;;

public class Routes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("fcrepo:fedora:SetSpec")
            .process(new TransformXmlToJsonProccessor())
            .to("fcrepo:fedora:SetSpec");

        from("fcrepo:fedora:OaiPmh?shema=http&host=sdvcmr-app03.slub-dresden.de&port=8080")
            .to("fcrepo:fedora:OaiPmh");

        // from("fcrepo://fedora:OaiPmh?shema=http&host=localhost&port=4711&user=fedoraAdmin&password=fedoraAdmin")
        // .process(new TransformXmlToJsonProccessor())
        // .to("fcrepo://fedora:OaiPmh")
        // .to("mock:test");
        // .to("elasticsearch://elasticsearch?ip=192.168.42.27&port=9300&operation=INDEX&indexName=fedora&indexType=mods")
        // .log("${body}").stop();
    }
}
