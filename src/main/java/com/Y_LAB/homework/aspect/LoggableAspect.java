package com.Y_LAB.homework.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.Y_LAB.homework.constants.DateTimePatternConstants.DATE_TIME_PATTERN;

//@Slf4j
@Aspect
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
