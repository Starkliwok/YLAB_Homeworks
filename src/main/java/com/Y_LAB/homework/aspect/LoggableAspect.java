package com.Y_LAB.homework.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


/**
 * Аспект для отображения информации о длительности выполнения всех методов
 * @author Денис Попов
 * @version 1.0
 */
@Aspect
@Component
public class LoggableAspect {

    @Pointcut("execution(* com.Y_LAB.homework..* (..))")
    public void anyMethod() {
    }

    @Around("anyMethod()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long time = System.currentTimeMillis() - start;
        System.out.println("Method: " + proceedingJoinPoint.getSignature() + " Execution time is " + time + " ms");
        return result;
    }
}
