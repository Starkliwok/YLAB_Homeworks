package com.Y_LAB.homework.condition;

import com.Y_LAB.homework.aop.annotation.EnableAudit;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class EnableAuditCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();

        if (beanFactory == null) {
            return false;
        }

        return !beanFactory.getBeansWithAnnotation(EnableAudit.class).isEmpty();
    }
}
