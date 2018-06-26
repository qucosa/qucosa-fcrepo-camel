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

package config;

import de.qucosa.config.DissTermsDao;
import de.qucosa.config.DissTermsMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DissTermsTest {
    private DissTermsDao termsDao;

    @Before
    public void init() {
        termsDao = new DissTermsDao(getClass().getResourceAsStream("/configuration/dissemination-config.json"));
    }

    @Test
    public void Dao_get_xml_namespace_oaidc() {
        DissTermsMapper.XmlNamespace namespace = termsDao.getXmlNamespace("oai_dc");
        Assert.assertEquals("oai_dc", namespace.getPrefix());
    }
}