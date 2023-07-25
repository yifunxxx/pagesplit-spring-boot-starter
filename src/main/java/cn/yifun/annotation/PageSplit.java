package cn.yifun.annotation;

import java.lang.annotation.*;

/**
 * @author yifun
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PageSplit {
}
