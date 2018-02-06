package de.qucosa.fcrepo.component;

import org.apache.camel.builder.RouteBuilder;;

public class Routes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // (from) load all configure setspecs from the config json file and
        // convert this data in the mapper object
        // (to) write all setspecs in the database
        // from("fcrepo:fedora:SetSpec").process(new TransformXmlToJsonProccessor()).to("fcrepo:fedora:SetSpec");

        // (from) load all exists identifieres from the fedora repo
        // (to) write identifieres in the database
        // from("fcrepo:fedora:OaiPmh?shema=http&host=sdvcmr-app03.slub-dresden.de&port=8080").to("fcrepo:fedora:OaiPmh");

        from("fcrepo:fedora:LoadDatastream?shema=http&host=sdvcmr-app03.slub-dresden.de&port=8080&metadataPrefix=xmetadissplus")
            .process(new MergeSetsRecordXmlProcessor())
            .log("${body}")
            .to("mock:test");

        // from("fcrepo://fedora:OaiPmh?shema=http&host=localhost&port=4711&user=fedoraAdmin&password=fedoraAdmin")
        // .process(new TransformXmlToJsonProccessor())
        // .to("fcrepo://fedora:OaiPmh")
        // .to("mock:test");
        // .to("elasticsearch://elasticsearch?ip=192.168.42.27&port=9300&operation=INDEX&indexName=fedora&indexType=mods")
        // .log("${body}").stop();
    }
}
