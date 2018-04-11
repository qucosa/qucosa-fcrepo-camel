package de.qucosa.fcrepo.component.mapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a json mapper class for mapping the list sets config file
 *
 * @author dseelig
 *
 */
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetsConfig {
    @JsonIgnore
    private SetSpecDao dao = null;
    
    @JsonProperty("sets")
    private List<Set> sets = new ArrayList<>();

    public List<Set> getSets() {
        return sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }

    public static class Set {
        @JsonProperty("setSpec")
        private String setSpec;

        @JsonProperty("setName")
        private String setName;

        @JsonProperty("predicate")
        private String predicate;

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
    
    @JsonIgnore
    public Set getSetObject(String setSpec) {
        return dao().getSetObject(setSpec);
    }
    
    @JsonIgnore
    public List<Set> getSetObjects() {
        return dao().getSetObjects();
    }
    
    @JsonIgnore
    public java.util.Set<String> getSetSpecs() {
        return dao().getSetSpecs();
    }
    
    private SetSpecDao dao() {
        
        if (dao == null) {
            dao = new SetSpecDao();
        }
        
        return dao;
    }
    
    private static class SetSpecDao {
        @SuppressWarnings("unused")
        private final Logger logger = LoggerFactory.getLogger(SetSpecDao.class);

        private List<Set> sets = null;
        
        public SetSpecDao() {
            ObjectMapper om = new ObjectMapper();
            File setSpecs = new File("/home/opt/camel-fedora-component/config/list-set-conf.json");

            try {
                sets = om.readValue(setSpecs, om.getTypeFactory().constructCollectionType(List.class, Set.class));
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        public List<Set> getSetObjects() {
            return sets;
        }

        public Set getSetObject(String setSpec) {
            Set setObj = null;

            for (Set obj : getSetObjects()) {

                if (obj.getSetSpec().equals(setSpec)) {
                    setObj = obj;
                    break;
                }
            }

            return setObj;
        }

        public java.util.Set<String> getSetSpecs() {
            java.util.Set<String> setSpecs = new HashSet<String>();

            for (int i = 0; i < getSetObjects().size(); i++) {
                Set set = getSetObjects().get(i);
                setSpecs.add(set.getSetSpec());
            }

            return setSpecs;
        }
    }
}
