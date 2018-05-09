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

package de.qucosa.component.oaipmh;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriParam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class OaiPmhEndpoint extends DefaultEndpoint {
    @UriParam
    private String verb = "ListIdentifiers";

    @UriParam
    private String from;

    @UriParam
    private String until;

    @UriParam
    private String set;

    @UriParam
    private String metadataPrefix = "oai_dc";

    @UriParam
    private long delay;

    public OaiPmhEndpoint(String uri, Component component) {
        super(uri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        throw new UnsupportedOperationException("The oai pmh endpoint is not an producer endpoint.");
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        Consumer consumer = null;

        switch (getVerb().toLowerCase()) {
            default:
                consumer = new IdentifiersPollConsumer(this, processor);
        }

        validateDateParameters();

        return consumer;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getUntil() {
        return until;
    }

    public void setUntil(String until) {
        this.until = until;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public void setMetadataPrefix(String metadataPrefix) {
        this.metadataPrefix = metadataPrefix;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }


    private void validateDateParameters() throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        df.setLenient(false);

        if (from != null && !from.isEmpty()) {
            try {
                df.parse(from);
            } catch (ParseException e) {
                throw new ParseException("The from parameter has wrong date format.", 1);
            }
        }

        if (until != null && !until.isEmpty()) {
            try {
                df.parse(until);
            } catch (ParseException e) {
                throw new ParseException("The until parameter has wrong date format.", 1);
            }
        }
    }
}
