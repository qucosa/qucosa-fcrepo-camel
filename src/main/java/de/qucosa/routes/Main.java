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

package de.qucosa.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.qucosa.component.fcrepo3.aggregate.RecordListAggregator;
import de.qucosa.component.oaiprovider.OaiProviderProcessor;
import de.qucosa.component.oaiprovider.model.DissTerms;
import de.qucosa.component.oaiprovider.model.RecordTransport;
import de.qucosa.component.oaiprovider.model.SetsConfig;
import de.qucosa.transformers.DcTransformer;
import de.qucosa.transformers.XMetaDissTransformer;
import org.apache.camel.BeanInject;
import org.apache.camel.Exchange;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.jackson.ListJacksonDataFormat;

import java.util.concurrent.TimeUnit;

public class Main extends RouteBuilder {

    @PropertyInject("update.delay")
    long updateDelay;

    @BeanInject("dissTerms")
    private DissTerms dissTerms;

    @BeanInject("setsConfig")
    private SetsConfig setsConfig;

    @Override
    public void configure() throws JsonProcessingException {

        Namespaces namespaces = new Namespaces("", "");

        for (DissTerms.XmlNamspace xmlNamspace : dissTerms.getSetXmlNamespaces()) {
            namespaces.add(xmlNamspace.getPrefix(), xmlNamspace.getUrl());
        }

        from("direct:oaiprovider")
                .id("oaiProviderProcess")
                .process(new OaiProviderProcessor())
                .aggregate(constant(true), new RecordListAggregator()).completionSize(2)
                .marshal(new ListJacksonDataFormat(RecordTransport.class))
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("http4:localhost:8080/qucosa-oai-provider/record/update");

        from("direct:dcdiss")
                .id("build-dc-dissemination")
                .setProperty("transfer.url.pattern", simple("{{transfer.url.pattern}}"))
                .setProperty("xsltStylesheetResourceName", simple("/xslt/mets2dcdata.xsl"))
                .setProperty("agent.name.substitutions", simple(""))
                .setProperty("transferUrlPidencode", simple("true"))
                .transform(new DcTransformer())
                .setProperty("format", simple("dc"))
                .to("direct:oaiprovider");

        from("direct:xmetadiss")
                .id("build-xmetadiss-dissemination")
                .setProperty("transfer.url.pattern", simple("{{transfer.url.pattern}}"))
                .setProperty("xsltStylesheetResourceName", simple("/xslt/mets2xmetadissplus.xsl"))
                .setProperty("agent.name.substitutions", simple(""))
                .setProperty("transferUrlPidencode", simple("true"))
                .transform(new XMetaDissTransformer())
                .setProperty("format", simple("xmetadiss"))
                .to("direct:oaiprovider");

        from("direct:update")
                .id("update-message-route")
                .resequence().body().timeout(TimeUnit.SECONDS.toMillis(updateDelay))
                .log("Perform updates for ${body}")
                .to("fcrepo3:METS?fedoraHosturl={{fedora.url}}&fedoraCredentials={{fedora.credentials}}")
                .split().body()
                .setProperty("pid", xpath("//mets:mets/@OBJID", String.class).namespaces(namespaces))
                .setProperty("lastmoddate", xpath("//mets:mets/mets:metsHdr/@LASTMODDATE", String.class).namespaces(namespaces))
                .multicast()
                .to("direct:dcdiss", "direct:xmetadiss")
                .end();

        from("activemq:topic:fedora.apim.update")
                .id("ActiveMQ-updates-route")
                .log("${body}")
                .transform(xpath("/atom:entry/atom:summary[@type='text']/text()")
                        .namespace("atom", "http://www.w3.org/2005/Atom"))
                .to("direct:update");

    }

}
