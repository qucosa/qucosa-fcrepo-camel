package de.qucosa.fcrepo.component;

import de.qucosa.fcrepo.component.mapper.DissTerms;

public class FcrepoConfiguration {
    private String endpointDef = "FindObjects";

	public String getEndpointDef() {
        return endpointDef;
    }

    public void setEndpointDef(String endpointDef) {
        this.endpointDef = endpointDef;
    }
    
    public DissTerms dissConf() {
        return new DissTerms();
    }
}
