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

import de.qucosa.oaiprovider.component.OaiProviderProcessor;
import de.qucosa.transformers.DcTransformer;
import de.qucosa.transformers.XMetaDissTransformer;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

import java.util.concurrent.TimeUnit;

public class Main extends RouteBuilder {

    @PropertyInject("update.delay")
    long updateDelay;

    @Override
    public void configure() {
        from("direct:oaiprovider")
                .id("oaiProviderProcess")
//                .startupOrder(1)
//                .setProperty("transfer.url.pattern", simple("{{transfer.url.pattern}}"))
                .process(new OaiProviderProcessor())
                .to("mock:test");

        from("direct:dcdiss")
                .id("build-dc-dissemination")
                .setProperty("transfer.url.pattern", simple("{{transfer.url.pattern}}"))
                .setProperty("xsltStylesheetResourceName", simple("/xslt/mets2dcdata.xsl"))
                .setProperty("agentNameSubstitutions", simple(""))
                .setProperty("transferUrlPidencode", simple("true"))
                .transform(new DcTransformer())
                .log("${body}")
                .setProperty("format", simple("dc"))
                .to("direct:oaiprovider");

        from("direct:xmetadiss")
                .id("build-xmetadiss-dissemination")
                .setProperty("transfer.url.pattern", simple("{{transfer.url.pattern}}"))
                .setProperty("xsltStylesheetResourceName", simple("/xslt/mets2xmetadissplus.xsl"))
                .setProperty("agentNameSubstitutions", simple(""))
                .setProperty("transferUrlPidencode", simple("true"))
                .transform(new XMetaDissTransformer())
                .log("${body}")
                .setProperty("format", simple("xmetadiss"))
                .to("direct:oaiprovider");

        from("direct:update")
                .id("update-message-route")
                .log("PID: ${body}")
                .resequence().body().timeout(TimeUnit.SECONDS.toMillis(updateDelay))
                .log("Perform updates for ${body}")
                .to("fcrepo3:METS?fedoraHosturl={{fedora.url}}&fedoraCredentials={{fedora.credentials}}")
                .split().body()
                .multicast()
                .to("direct:dcdiss", "direct:xmetadiss");


        from("activemq:topic:fedora.apim.update")
                .id("ActiveMQ-updates-route")
                .log("${body}")
                .transform(xpath("/atom:entry/atom:summary[@type='text']/text()")
                        .namespace("atom", "http://www.w3.org/2005/Atom"))
                .to("direct:update");

    }

}
