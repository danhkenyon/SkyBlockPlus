package uk.ac.bsfc.sbp.utils.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Comments {
    Comment[] value();
}
