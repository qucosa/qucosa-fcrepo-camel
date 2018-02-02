package de.qucosa.fcrepo.component;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface EndpointDefAnnotation {
    boolean isProducer();;
    
    boolean isConsumer();
}
