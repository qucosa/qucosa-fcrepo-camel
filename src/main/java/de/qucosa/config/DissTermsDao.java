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

package de.qucosa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.model.DissTerms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DissTermsDao {

    private Logger logger = LoggerFactory.getLogger(DissTerms.class);

    private InputStream config;

    private DissTermsMapper mapping;

    public DissTermsDao(String path) throws FileNotFoundException {
        this(new File(path));
    }

    public DissTermsDao(File file) throws FileNotFoundException {
        this((InputStream) new FileInputStream(file));
    }

    public DissTermsDao(InputStream stream) {
        this.config = stream;

        ObjectMapper om = new ObjectMapper();

        try {
            mapping = om.readValue(config, DissTermsMapper.class);
        } catch (IOException e) {
            logger.error("Cannot parse dissemination-conf JSON file.");
        }
    }

    public Map<String, String> getMapXmlNamespaces() {
        HashSet<DissTerms.XmlNamspace> xmlNamespaces = (HashSet<DissTerms.XmlNamspace>) mapping.getXmlnamespaces();
        Map<String, String> map = new HashMap<>();

        for (DissTerms.XmlNamspace namspace : xmlNamespaces) {
            map.put(namspace.getPrefix(), namspace.getUrl());
        }

        return map;
    }

    public Set<DissTerms.XmlNamspace> getSetXmlNamespaces() { return mapping.getXmlnamespaces(); }

    public DissTerms.XmlNamspace getXmlNamespace(String prefix) {
        DissTerms.XmlNamspace xmlNamspace = null;

        for (DissTerms.XmlNamspace namespace : mapping.getXmlnamespaces()) {

            if (namespace.getPrefix().equals(prefix)) {
                xmlNamspace = namespace;
            }
        }

        return xmlNamspace;
    }

    public DissTerms.Term getTerm(String diss, String name) {
        HashSet<DissTerms.DissTerm> dissTerms = (HashSet<DissTerms.DissTerm>) mapping.getDissTerms();
        DissTerms.Term term = null;

        for (DissTerms.DissTerm dt : dissTerms) {

            if (!dt.getDiss().equals(diss)) {
                logger.error(diss + " is does not exists in dissemination-config.");
                continue;
            }

            if (dt.getTerms().isEmpty()) {
                logger.error(diss + " has no terms config.");
                continue;
            }

            for (DissTerms.Term t : dt.getTerms()) {

                if (!t.getName().equals(name)) {
                    logger.error("The term name " + name + " is not available in dissemination " + diss);
                    continue;
                }

                term = t;
            }

            if (term != null) {
                break;
            }
        }

        return term;
    }

    public Set<DissTerms.DissFormat> getFormats() { return mapping.getFormats(); }

    public DissTerms.DissFormat getFormat(String format) {
        DissTerms.DissFormat dissFormat = null;

        for (DissTerms.DissFormat df : mapping.getFormats()) {

            if (df.getMdprefix().equals(format)) {
                dissFormat = df;
                break;
            }
        }

        return dissFormat;
    }

}
