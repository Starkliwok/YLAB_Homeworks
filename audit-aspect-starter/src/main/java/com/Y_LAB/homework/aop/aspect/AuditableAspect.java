package com.Y_LAB.homework.aop.aspect;

import com.Y_LAB.homework.aop.annotation.Auditable;
import com.Y_LAB.homework.aop.model.audit.Audit;
import com.Y_LAB.homework.aop.model.audit.AuditResult;
import com.Y_LAB.homework.aop.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Аспект для обработки действий пользователя
 * @author Денис Попов
 * @version 1.0
 */
@RequiredArgsConstructor
@Aspect
public class AuditableAspect {

    /** Поле ДАО слоя аудитов*/
    private final AuditService auditService;

    /** Срез для обнаружения методов помеченных аннотацией {@link Auditable}*/
    @Pointcut("within(@com.Y_LAB.homework.aop.annotation.Auditable *) || " +
            "execution(@com.Y_LAB.homework.aop.annotation.Auditable * *(..))) && execution(* *(..))")
    public void annotatedByAuditable() {}

    /**
     * Метод обрабатывает методы помеченные аннотацией {@link Auditable},
     * в случае успешного завершения метода сохраняет аудит пользователя с результатом {@link AuditResult#SUCCESS}
     * если во время выполнения метода выбрасывается исключение, то метод сохраняет аудит пользователя с результатом
     * {@link AuditResult#FAIL}
     * @param jp JoinPoint
     * @return Возвращаемое значение обрабатываемого метода
     * @throws Throwable Пробрасывает исключение если его выбрасывает обрабатываемый метод
     */
    @Around("annotatedByAuditable()")
    public Object logMethod(ProceedingJoinPoint jp) throws Throwable {
        String className = jp.getSignature().getDeclaringType().getSimpleName();
        String methodName = jp.getSignature().getName();
        Object[] args = jp.getArgs();
        Object result = null;
        Throwable throwable = null;
        AuditResult auditResult = AuditResult.SUCCESS;
        try {
            result = jp.proceed();
        } catch (Throwable e) {
            auditResult = AuditResult.FAIL;
            throwable = e;
        }
        switch (methodName) {
            case "getAllUserReservations" -> {
                long userId = (long) args[0];
                auditService.saveAudit(new Audit(userId, className, methodName, auditResult));
            }
            case "getUserReservation", "deleteUserReservation" -> {
                long reservationId = (long) args[0];
                long userId = (long) args[1];
                auditService.saveAudit(new Audit(userId, className, methodName + "(" + reservationId + ")", auditResult));
            }
            case "saveReservation" -> {
                long userId = (long) args[1];
                auditService.saveAudit(new Audit(userId, className, methodName + "()", auditResult));
            }
        }
        if (auditResult.equals(AuditResult.FAIL)) {
            throw throwable;
        }
        return result;
    }
}