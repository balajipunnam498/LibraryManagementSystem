package com.task.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.task.serviceimpl..*(..))")
    public Object logBillServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget()
                                    .getClass()
                                    .getSimpleName();
        String methodName = joinPoint.getSignature()
        		.getName();

        long start = System.currentTimeMillis();

        log.info("Entering {}.{}() with args={}",
                className,
                methodName,
                Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();

            long timeTaken = System.currentTimeMillis() - start;

            log.info("Exiting {}.{}() | Time={} ms",
                    className,
                    methodName,
                    timeTaken);

            return result;

        } catch (Exception ex) {

            log.error("Exception in {}.{}() | Message={}",
                    className,
                    methodName,
                    ex.getMessage(),
                    ex);

            throw ex;
        }
    }
    
    @Around("execution(* com.task.serviceimpl.BookServiceIMPL.*(..))")
    public Object logBookServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget()
                                    .getClass()
                                    .getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        log.info("Entering {}.{}() with args={}",
                className,
                methodName,
                Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();

            long timeTaken = System.currentTimeMillis() - startTime;

            log.info("Exiting {}.{}() | Time={} ms",
                    className,
                    methodName,
                    timeTaken);

            return result;

        } catch (Exception ex) {

            log.error("Exception in {}.{}() | Message={}",
                    className,
                    methodName,
                    ex.getMessage(),
                    ex);

            throw ex;
        }
    }
    
    @Around("execution(* com.task.serviceimpl.LibrarianServiceImpl.*(..))")
    public Object logLibrarianServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget()
                                    .getClass()
                                    .getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        log.info("➡️ Entering {}.{}() with args={}",
                className,
                methodName,
                Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();

            long timeTaken = System.currentTimeMillis() - startTime;

            log.info("Exiting {}.{}() | Time={} ms",
                    className,
                    methodName,
                    timeTaken);

            return result;

        } catch (Exception ex) {

            log.error("Exception in {}.{}() | Message={}",
                    className,
                    methodName,
                    ex.getMessage(),
                    ex);

            throw ex;
        }
    }
    
    @Around("execution(* com.task.serviceimpl.MemberServiceImpl.*(..))")
    public Object logMemberServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget()
                                    .getClass()
                                    .getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        log.info("Entering {}.{}() with args={}",
                className,
                methodName,
                Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();

            long timeTaken = System.currentTimeMillis() - startTime;

            log.info("Exiting {}.{}() | Time={} ms",
                    className,
                    methodName,
                    timeTaken);

            return result;

        } catch (Exception ex) {

            log.error("Exception in {}.{}() | Message={}",
                    className,
                    methodName,
                    ex.getMessage(),
                    ex);

            throw ex;
        }
    }
    @Around("execution(* com.task.serviceimpl.TransactionServiceImpl.*(..))")
    public Object logTransactionServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget()
                                    .getClass()
                                    .getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        log.info("Entering {}.{}() with args={}",
                className,
                methodName,
                Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();

            long timeTaken = System.currentTimeMillis() - startTime;

            log.info("Exiting {}.{}() | Time={} ms",
                    className,
                    methodName,
                    timeTaken);

            return result;

        } catch (Exception ex) {

            log.error("Exception in {}.{}() | Message={}",
                    className,
                    methodName,
                    ex.getMessage(),
                    ex);

            throw ex;
        }
    }
}
