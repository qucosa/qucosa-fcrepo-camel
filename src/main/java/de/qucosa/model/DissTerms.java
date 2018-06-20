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

package de.qucosa.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class DissTerms {
    @JsonIgnore
    private Logger logger = LoggerFactory.getLogger(DissTerms.class);

    @JsonIgnore
    private InputStream config;

    @JsonIgnore
    private DissTermsDao dao = null;

    @JsonProperty("xmlnamespaces")
    private Set<XmlNamspace> xmlnamespaces;

    @JsonProperty("dissTerms")
    private Set<DissTerm> dissTerms;

    @JsonProperty("formats")
    private Set<DissFormat> formats;

    @JsonCreator
    public <T> DissTerms(@JsonProperty("config") T config) {

        if (config instanceof String) {
            this.config = getClass().getResourceAsStream((String) config);

            if (this.config == null) {

                try {
                    this.config = new FileInputStream(new File((String) config));
                } catch (FileNotFoundException e) {
                    logger.error("dissemination-config.json file not found.", e);
                }
            }
        }

        if (config instanceof InputStream) {
            this.config = (InputStream) config;
        }

        if (config instanceof File) {

            try {
                this.config = new FileInputStream((File) config);
            } catch (FileNotFoundException e) {
                logger.error("dissemination-config.json file not found.", e);
            }
        }
    }

    public Set<XmlNamspace> getXmlnamespaces() {
        return xmlnamespaces;
    }

    public void setXmlnamespaces(Set<XmlNamspace> xmlnamespaces) {
        this.xmlnamespaces = xmlnamespaces;
    }

    public Set<DissTerm> getDissTerms() {
        return dissTerms;
    }

    public void setDissTerms(Set<DissTerm> dissTerms) {
        this.dissTerms = dissTerms;
    }

    public Set<DissFormat> getFormats() {
        return formats;
    }

    public void setFormats(Set<DissFormat> formats) {
        this.formats = formats;
    }

    @JsonIgnore
    public Map<String, String> getMapXmlNamespaces() {
        return dao().getMapXmlNamespaces();
    }

    @JsonIgnore
    public XmlNamspace getXmlNamespace(String prefix) {
        return dao().getXmlNamespace(prefix);
    }

    @JsonIgnore
    public Term getTerm(String diss, String name) {
        return dao().getTerm(diss, name);
    }

    @JsonIgnore
    public Set<DissFormat> formats() {
        return dao().dissFormats();
    }

    @JsonIgnore
    public Set<XmlNamspace> getSetXmlNamespaces() { return dao().getSetXmlNamespaces(); }

    public static class XmlNamspace {
        @JsonProperty("prefix")
        private String prefix;

        @JsonProperty("url")
        private String url;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class DissTerm {
        @JsonProperty("diss")
        private String diss;

        @JsonProperty("terms")
        private Set<Term> terms;

        public String getDiss() {
            return diss;
        }

        public void setDiss(String diss) {
            this.diss = diss;
        }

        public Set<Term> getTerms() {
            return terms;
        }

        public void setTerms(Set<Term> terms) {
            this.terms = terms;
        }
    }

    public static class Term {
        @JsonProperty("name")
        private String name;

        @JsonProperty("term")
        private String term;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }
    }

    public static class DissFormat {
        @JsonProperty("mdprefix")
        private String mdprefix;

        @JsonProperty("schemaurl")
        private String schemaUrl;

        @JsonProperty("namespace")
        private String namespace;

        public String getMdprefix() { return mdprefix; }

        public void setMdprefix(String mdprefix) { this.mdprefix = mdprefix; }

        public String getSchemaUrl() { return schemaUrl; }

        public void setSchemaUrl(String schemaUrl) { this.schemaUrl = schemaUrl; }

        public String getNamespace() { return namespace; }

        public void setNamespace(String namespace) { this.namespace = namespace; }
    }

    private DissTermsDao dao() {

        if (dao == null) {
            dao = new DissTermsDao(config);
        }

        return dao;
    }

    private class DissTermsDao {
        private final Logger logger = LoggerFactory.getLogger(DissTermsDao.class);

        DissTerms dissTerms = null;

        public DissTermsDao(InputStream stream) {
            ObjectMapper om = new ObjectMapper();

            try {
                dissTerms = om.readValue(stream, DissTerms.class);
            } catch (IOException e) {
                logger.error("Cannot parse dissemination-conf JSON file.");
            }
        }


        public Map<String, String> getMapXmlNamespaces() {
            HashSet<XmlNamspace> xmlNamespaces = (HashSet<XmlNamspace>) dissTerms.getXmlnamespaces();
            Map<String, String> map = new HashMap<>();

            for (XmlNamspace namspace : xmlNamespaces) {
                map.put(namspace.getPrefix(), namspace.getUrl());
            }

            return map;
        }

        public Set<XmlNamspace> getSetXmlNamespaces() {
            return xmlNamespaces();
        }

        public XmlNamspace getXmlNamespace(String prefix) {
            XmlNamspace xmlNamspace = null;

            for (XmlNamspace namespace : xmlNamespaces()) {

                if (namespace.getPrefix().equals(prefix)) {
                    xmlNamspace = namespace;
                }
            }

            return xmlNamspace;
        }

        public Term getTerm(String diss, String name) {
            HashSet<DissTerm> dissTerms = (HashSet<DissTerm>) this.dissTerms.getDissTerms();
            Term term = null;

            for (DissTerm dt : dissTerms) {

                if (!dt.getDiss().equals(diss)) {
                    logger.error(diss + " is does not exists in dissemination-config.");
                    continue;
                }

                if (dt.getTerms().isEmpty()) {
                    logger.error(diss + " has no terms config.");
                    continue;
                }

                for (Term t : dt.getTerms()) {

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

        public Set<DissFormat> dissFormats() {
            return dissTerms.getFormats();
        }

        public DissFormat dissFormat(String format) {
            DissFormat dissFormat = null;

            for (DissFormat df : dissTerms.getFormats()) {

                if (df.getMdprefix().equals(format)) {
                    dissFormat = df;
                    break;
                }
            }

            return dissFormat;
        }

        private Set<XmlNamspace> xmlNamespaces() {
            return dissTerms.getXmlnamespaces();
        }
    }

}
