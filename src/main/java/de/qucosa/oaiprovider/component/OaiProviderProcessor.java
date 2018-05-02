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

package de.qucosa.oaiprovider.component;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class OaiProviderProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

    }

/*
    @SuppressWarnings("unused")
    private RecordTransport buildEpicurObject(Document metsDoc) throws Exception {
        EpicurDissMapper mapper = new EpicurDissMapper("http://test.##AGENT##.qucosa.de/id/##PID##", "", "", true);
        Document epicurRes = mapper.transformEpicurDiss(metsDoc);
        XPath xPath = DocumentXmlUtils.xpath(dt.getMapXmlNamespaces());
        DocumentXmlUtils.resultXml(buildRecord(epicurRes, metsDoc, "epicur"));

        epicur.setPid(metsXml.pid());
        epicur.setModified(DateTimeConverter.timestampWithTimezone(metsXml.lastModDate()));
        epicur.setPrefix("epicur");
        epicur.setData(buildRecord(epicurRes, metsDoc, "epicur"));
        epicur.setOaiId("");
        
        return epicur;
    }
*/
}
