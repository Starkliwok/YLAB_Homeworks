package com.Y_LAB.homework.starter.aspect;

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

    /**Срез для обнаружения всех методов*/
    @Pointcut("execution(* com.Y_LAB.homework..* (..))")
    public void anyMethod() {
    }

    /**
     * Метод обрабатывает все методы, которые попадают под срез {@link LoggableAspect#anyMethod()},
     * в случае успешного завершения метода выводит информации о времени работы обрабатываемого метода
     * если во время выполнения метода выбрасывается исключение, то метод пробрасывает исключение дальше
     * @param proceedingJoinPoint JoinPoint
     * @return Возвращаемое значение обрабатываемого метода
     * @throws Throwable Пробрасывает исключение если его выбрасывает обрабатываемый метод
     */
    @Around("anyMethod()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long time = System.currentTimeMillis() - start;
        System.out.println("Method: " + proceedingJoinPoint.getSignature() + " Execution time is " + time + " ms");
        return result;
    }
}
