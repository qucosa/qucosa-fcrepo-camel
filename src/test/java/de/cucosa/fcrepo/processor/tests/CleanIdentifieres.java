package de.cucosa.fcrepo.processor.tests;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class CleanIdentifieres {
    
    @Test
    public void cleanIdentifires_Test() {
        Set<String> ids = new HashSet<>();
        ids.add("qucosa:123");
        ids.add("qucosa:456");
        ids.add("qucosa:789");
        
        System.out.println(ids);
        
        ids.add("qucosa:1011");
        ids.add("qucosa:123");
        
        System.out.println(ids);
    }
}
