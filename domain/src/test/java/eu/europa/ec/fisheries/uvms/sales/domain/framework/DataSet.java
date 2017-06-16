package eu.europa.ec.fisheries.uvms.sales.domain.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by MATBUL on 3/02/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataSet {

    String initialData() default "";
    String expectedData() default "";

}