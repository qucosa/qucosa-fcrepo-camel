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

package de.qucosa.transformers;

import de.qucosa.dissemination.epicur.EpicurBuilderException;
import de.qucosa.dissemination.epicur.EpicurDissMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class EpicurTransformer implements Expression {

    private Logger logger = LoggerFactory.getLogger(EpicurTransformer.class);

    @Override
    public <T> T evaluate(Exchange exchange, Class<T> type) {
        EpicurDissMapper epicurDissMapper = new EpicurDissMapper(exchange.getProperty("transfer.url.pattern").toString(),
                exchange.getProperty("frontpage.url.pattern").toString(),
                exchange.getProperty("agent.name.substitutions").toString(),
                Boolean.valueOf(exchange.getProperty("transferUrlPidencode").toString()));

        try {
            Document document = epicurDissMapper.transformEpicurDiss(exchange.getIn().getBody(Document.class));
            exchange.getIn().setBody(document);
        } catch (JAXBException | EpicurBuilderException | ParserConfigurationException | SAXException | IOException e) {
            logger.error("Cannot build epicur document.", e);
        } catch (TransformerException e) {
            logger.error("Cannot transform epicur dissemination.");
        }

        exchange.setProperty("pid", exchange.getProperty("pid"));
        exchange.setProperty("lastmoddate", exchange.getProperty("lastmoddate"));

        return (T) exchange.getIn().getBody();
    }
}
