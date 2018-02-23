package de.qucosa.fcrepo.component.pjos.oaiprivider;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Set implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("setSpec")
    private String setSpec;
    
    @JsonProperty("setName")
    private String setName;
    
    @JsonProperty("predicate")
    private String predicate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSetSpec() {
        return setSpec;
    }

    public void setSetSpec(String setSpec) {
        this.setSpec = setSpec;
    }
    
    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }
}
