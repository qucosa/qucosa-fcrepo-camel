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
