package com.Y_LAB.homework.config;

import com.Y_LAB.homework.aop.aspect.AuditableAspect;
import com.Y_LAB.homework.aop.dao.AuditDAO;
import com.Y_LAB.homework.aop.dao.impl.AuditDAOImpl;
import com.Y_LAB.homework.aop.service.AuditService;
import com.Y_LAB.homework.aop.service.impl.AuditServiceImpl;
import com.Y_LAB.homework.condition.EnableAuditCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.sql.DataSource;
import java.sql.SQLException;


@Configuration
@Conditional(EnableAuditCondition.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@RequiredArgsConstructor
public class AuditConfig {

    private final DataSource dataSource;

    @Bean
    public AuditableAspect auditableAspect() throws SQLException {
        return new AuditableAspect(auditService());
    }

    @Bean
    public AuditService auditService() throws SQLException {
        return new AuditServiceImpl(auditDAO());
    }

    @Bean
    public AuditDAO auditDAO() throws SQLException {
        return new AuditDAOImpl(dataSource);
    }
}