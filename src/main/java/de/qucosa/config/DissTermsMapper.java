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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.qucosa.model.DissTerms;

import java.io.Serializable;
import java.util.Set;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class DissTermsMapper implements Serializable {
    @JsonProperty("xmlnamespaces")
    private Set<DissTerms.XmlNamspace> xmlnamespaces;

    @JsonProperty("dissTerms")
    private Set<DissTerms.DissTerm> dissTerms;

    @JsonProperty("formats")
    private Set<DissTerms.DissFormat> formats;

    public Set<DissTerms.XmlNamspace> getXmlnamespaces() { return xmlnamespaces; }

    public void setXmlnamespaces(Set<DissTerms.XmlNamspace> xmlnamespaces) { this.xmlnamespaces = xmlnamespaces; }

    public Set<DissTerms.DissTerm> getDissTerms() { return dissTerms; }

    public void setDissTerms(Set<DissTerms.DissTerm> dissTerms) { this.dissTerms = dissTerms; }

    public Set<DissTerms.DissFormat> getFormats() { return formats; }

    public void setFormats(Set<DissTerms.DissFormat> formats) { this.formats = formats; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class XmlNamspace {
        @JsonProperty("prefix")
        private String prefix;

        @JsonProperty("url")
        private String url;

        public String getPrefix() { return prefix; }

        public void setPrefix(String prefix) { this.prefix = prefix; }

        public String getUrl() { return url; }

        public void setUrl(String url) { this.url = url; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DissTerm {
        @JsonProperty("diss")
        private String diss;

        @JsonProperty("terms")
        private Set<DissTerms.Term> terms;

        public String getDiss() { return diss; }

        public void setDiss(String diss) { this.diss = diss; }

        public Set<DissTerms.Term> getTerms() { return terms; }

        public void setTerms(Set<DissTerms.Term> terms) { this.terms = terms; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Term {
        @JsonProperty("name")
        private String name;

        @JsonProperty("term")
        private String term;

        public String getName() { return name; }

        public void setName(String name) { this.name = name; }

        public String getTerm() { return term; }

        public void setTerm(String term) { this.term = term; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
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
}
