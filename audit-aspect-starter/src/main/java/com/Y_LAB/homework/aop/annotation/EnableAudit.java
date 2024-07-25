package com.Y_LAB.homework.aop.annotation;

import com.Y_LAB.homework.config.AuditConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({AuditConfig.class})
public @interface EnableAudit {
}
