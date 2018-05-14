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

package oaipmh;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class OaiPmhIdentifierPollingConsumerTest extends CamelTestSupport {
    @Test
    public void Actualization_token_by_poll_interval() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:result");
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {
            public void configure() {
                from("oaipmh:?url=http://192.168.42.28:8080/fedora&verb=ListIdentifiers&metatdataPrefix=oai_dc&delay=2000&")
                        //.to("stream:out");
                        .to("mock:result");
            }
        };
    }
}
