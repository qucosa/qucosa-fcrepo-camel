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
import de.qucosa.oaiprovider.component.model.DissTerms;
import de.qucosa.oaiprovider.component.model.SetsConfig;
import org.apache.camel.builder.RouteBuilder;

import java.util.concurrent.TimeUnit;

public class Main extends RouteBuilder {

    @Override
    public void configure() {
        DissTerms dt = new DissTerms();
        SetsConfig sets = new SetsConfig();

        from("direct:oaiprovider")
                .id("oaiProviderProcess")
                .startupOrder(1)
                .process(new OaiProviderProcessor(dt, sets))
                .to("oaiprovider:update");

        long updateDelay = TimeUnit.SECONDS.toMillis(2);

        from("direct:update")
                .id("update-message-route")
                .startupOrder(4)
                .resequence(body())
                .timeout(updateDelay)
                .to("fcrepo:fedora:METS?shema=http&host=${fedora.host}&port=${fedora.port}")
                .to("direct:oaiprovider");

        from("fcrepo:fedora:OaiPmh?shema=http&host=${fedora.host}&port=${fedora.port}")
                .id("fedoraOai")
                .startupOrder(5)
                .split().body()
                .to("direct:aggregateIdents");

        from("activemq:topic:fedora.apim.update")
                .id("ActiveMQ-updates-route")
                .startupOrder(6)
                .transform(xpath("/atom:entry/atom:summary[@type='text']/text()")
                        .namespace("atom", "http://www.w3.org/2005/Atom"))
                .to("direct:update");

    }

}
