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

package de.qucosa.component.fcrepo3;

import de.qucosa.config.DissTermsDao;
import de.qucosa.model.SetsConfig;

public class Fcrepo3Configuration {
    private String endpointDef = "FindObjects";

    private DissTermsDao dissTerms = null;

    private SetsConfig sets = null;

    public String getEndpointDef() {
        return endpointDef;
    }

    public void setEndpointDef(String endpointDef) {
        this.endpointDef = endpointDef;
    }

    public DissTermsDao getDissConf() {
        return dissTerms;
    }

    public void setDissConf(DissTermsDao dissTerms) {
        this.dissTerms = dissTerms;
    }

    public SetsConfig getSets() {
        return sets;
    }

    public void setSets(SetsConfig sets) {
        this.sets = sets;
    }
}
