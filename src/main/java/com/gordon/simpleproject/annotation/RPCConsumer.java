package com.gordon.simpleproject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description :
 * @Author : Gordon Deng
 * @Date :   02:13 2021/5/28
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Autowired
public @interface RPCConsumer {
	
	String serviceVersion() default "1.0.0";
}
