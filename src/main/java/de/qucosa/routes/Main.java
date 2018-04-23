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

import java.util.concurrent.TimeUnit;

import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

public class Main extends RouteBuilder {

    @PropertyInject("update.delay")
    long updateDelay;
    
    @PropertyInject("fedora.host")
    private String fedoraHost;
    
    @PropertyInject("fedora.schema")
    private String fedoraSchema;
    
    @PropertyInject("fedora.port")
    private String fedoraPort;
    
    @Override
    public void configure() {
//        from("direct:oaiprovider")
//                .id("oaiProviderProcess")
//                .startupOrder(1)
//                .process(new OaiProviderProcessor())
//                .to("fcrepo:fedora:OaiProvider");

        from("direct:update")
                .id("update-message-route")
                .log("PID: ${body}")
                .resequence().body().timeout(TimeUnit.SECONDS.toMillis(updateDelay))
                .log("Perform updates for ${body}")
                .to("fcrepo3:METS?schema=" + fedoraSchema + "&host=" + fedoraHost + "&port=" + fedoraPort + "");
//                .to("direct:oaiprovider");


        from("activemq:topic:fedora.apim.update")
                .id("ActiveMQ-updates-route")
                .log("${body}")
                .transform(xpath("/atom:entry/atom:summary[@type='text']/text()")
                        .namespace("atom", "http://www.w3.org/2005/Atom"))
                .to("direct:update");

    }

}
