package demo.util.concurrent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Flag for a method that it's not for concurrent access
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface ForSingleThread {

}
