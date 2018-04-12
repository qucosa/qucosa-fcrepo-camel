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

package de.qucosa.fcrepo.builders;

import de.qucosa.oaiprovider.component.builders.RecordXmlBuilder;
import de.qucosa.oaiprovider.component.model.DissTerms;
import de.qucosa.fcrepo.component.mapper.MetsXmlMapper;
import de.qucosa.oaiprovider.component.model.SetsConfig;
import de.qucosa.fcrepo.component.transformers.DcDissTransformer;
import de.qucosa.utils.DocumentXmlUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerFactoryConfigurationError;

public class RecordXmlBuilderTest {
    private DissTerms dissTerms = new DissTerms();

    private SetsConfig sets = new SetsConfig();

    @Test
    @Ignore("Does not work")
    public void buildRecordDC_Test() throws TransformerFactoryConfigurationError, Exception {
        DcDissTransformer dissTransformer = new DcDissTransformer(
                "/xml/mets2dcdata.xsl",
                "http://##AGENT##.example.com/##PID##/content.zip",
                "",
                true);
        Document metsDoc = DocumentXmlUtils.document(getClass().getClassLoader().getResource("xml/hochschulschrift_TEST.xml").getPath(), true);
        Document recordTemplate = DocumentXmlUtils.document(getClass().getClassLoader().getResource("xml/record.xml").getPath(), true);
        MetsXmlMapper metsXml = new MetsXmlMapper(metsDoc, dissTerms.getMapXmlNamespaces());

        RecordXmlBuilder builder = new RecordXmlBuilder(dissTransformer.transformDcDiss(metsDoc), recordTemplate)
                .setMetsDocument(metsDoc)
                .setDissTerms(dissTerms)
                .setSets(sets)
                .setFormat("dc");
        Document record = builder.buildRecord(metsXml);

        System.out.println(DocumentXmlUtils.resultXml(record));
    }
}