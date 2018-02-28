package de.qucosa.fcrepo.component;

import de.qucosa.fcrepo.component.mapper.DissTerms;

public class FcrepoConfiguration {
    private String endpointDef = "FindObjects";

    private DissTerms dissTerms = null;
    
    public String getEndpointDef() {
        return endpointDef;
    }

    public void setEndpointDef(String endpointDef) {
        this.endpointDef = endpointDef;
    }
    
    public DissTerms getDissConf() {
        return dissTerms;
    }

    public void setDissConf(DissTerms dissTerms) {
        this.dissTerms = dissTerms;
    }
}
