/*
 * Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.qucosa.fcrepo.component;

import de.qucosa.oaiprovider.component.model.DissTerms;
import de.qucosa.oaiprovider.component.model.SetsConfig;
import de.qucosa.fcrepo.component.xml.utils.DocumentXmlUtils;
import de.qucosa.oaiprovider.component.OaiProviderProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.ByteArrayInputStream;

;

public class Routes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        DissTerms dt = new DissTerms();
        SetsConfig sets = new SetsConfig();

        from("direct:oaiprovider")
                .id("oaiProviderProcess")
//            .autoStartup(false)
                .startupOrder(1)
                .process(new OaiProviderProcessor(dt, sets))
                .to("fcrepo:fedora:OaiProvider");

        from("direct:reportingDB")
                .id("reportingDBProcess")
//            .autoStartup(false)
                .startupOrder(2)
                .process(new ReportingDbProcessor())
                .to("fcrepo:fedora:ReportingDb");

        // @todo replace the mock endpoint with elastic serach endpoint
        from("direct:qucosaelastic")
                .id("elasticSearchProcess")
//            .autoStartup(false)
                .startupOrder(3)
                .process(new QucosaElasticSearchProcessor())
                .to("mock:test");

        from("direct:aggregateIdents")
                .id("cleanIdentifires")
//            .autoStartup(false)
                .startupOrder(4)
                .resequence().body()
                .to("fcrepo:fedora:METS?shema=http&host=192.168.42.28&port=8080")
                .filter().body().multicast()
                .to("direct:oaiprovider", "direct:reportingDB", "direct:qucosaelastic")
                .end();

        // test dev host sdvcmr-app03.slub-dresden.de
        from("fcrepo:fedora:OaiPmh?shema=http&host=192.168.42.28&port=8080")
                .id("fedoraOai")
                .startupOrder(5)
//            .autoStartup(false)
                .split().body()
                .to("direct:aggregateIdents");

        from("activemq:topic:fedora.apim.update")
                .id("fedoraJms")
                .startupOrder(6)
//            .autoStartup(false)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String msg = (String) exchange.getIn().getBody();
                        Document document = DocumentXmlUtils.document(new ByteArrayInputStream(msg.getBytes("UTF-8")), false);
                        XPath xPath = DocumentXmlUtils.xpath(dt.getMapXmlNamespaces());
                        String id = (String) xPath.compile("//summary[@type=\"text\"]/text()").evaluate(document, XPathConstants.STRING);
                        exchange.getIn().setBody(id);
                    }
                })
                .to("direct:aggregateIdents");


        // .to("elasticsearch://elasticsearch?ip=192.168.42.27&port=9300&operation=INDEX&indexName=fedora&indexType=mods")
    }
}
