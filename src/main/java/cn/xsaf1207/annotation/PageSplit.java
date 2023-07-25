package cn.xsaf1207.annotation;

import java.lang.annotation.*;

/**
 * @author yifun
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PageSplit {
}
