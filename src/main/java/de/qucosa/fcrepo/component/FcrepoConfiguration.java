package de.qucosa.fcrepo.component;

import de.qucosa.fcrepo.component.mapper.DissTerms;
import de.qucosa.fcrepo.component.mapper.SetsConfig;

public class FcrepoConfiguration {
    private String endpointDef = "FindObjects";

    private DissTerms dissTerms = null;
    
    private SetsConfig sets = null;
    
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
    
    public SetsConfig getSets() {
        return sets;
    }

    public void setSets(SetsConfig sets) {
        this.sets = sets;
    }
}
