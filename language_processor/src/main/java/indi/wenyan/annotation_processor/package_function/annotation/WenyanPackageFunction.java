package indi.wenyan.annotation_processor.package_function.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface WenyanPackageFunction {
    String value();

    boolean threadSafe() default false;
}
