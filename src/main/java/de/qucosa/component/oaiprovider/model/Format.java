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

package de.qucosa.component.oaiprovider.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Format {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("mdprefix")
    private String mdprefix;

    @JsonProperty("lastpolldate")
    private Timestamp lastpolldate;

    @JsonProperty("dissType")
    private String dissType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMdprefix() {
        return mdprefix;
    }

    public void setMdprefix(String mdprefix) {
        this.mdprefix = mdprefix;
    }

    public Timestamp getLastpolldate() {
        return lastpolldate;
    }

    public void setLastpolldate(Timestamp lastpolldate) {
        this.lastpolldate = lastpolldate;
    }

    public String getDissType() {
        return dissType;
    }

    public void setDissType(String dissType) {
        this.dissType = dissType;
    }
}