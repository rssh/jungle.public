package ua.gradsoft.jungle.auth.server;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation, which show that parameter with number <code> value </code> (starting
 * from 0) mens userId and must be substituted to UserServerContext in server middleware.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UserIdParameter {

    int value() default 0;

}
