package de.qucosa.fcrepo.component;

import org.apache.camel.builder.RouteBuilder;;

public class Routes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // (from) load all configure setspecs from the config json file and convert this data in the mapper object
        // (to) write all setspecs in the database
        from("fcrepo:fedora:SetSpec").to("fcrepo:fedora:SetSpec");

        // (from) load all exists identifieres from the fedora repo
        // (to) write identifieres in the database
//        from("fcrepo:fedora:OaiPmh?shema=http&host=sdvcmr-app03.slub-dresden.de&port=8080")
//            .to("fcrepo:fedora:OaiPmh");

        // (from) load all format definitions from fedora SDef/objectXML
        // (to) save formats and method definitions in database
//        from("fcrepo:fedora:QucosaObjects?shema=http&host=sdvcmr-app03.slub-dresden.de&port=8080")
//            .to("fcrepo:fedora:QucosaObjects");

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
}
