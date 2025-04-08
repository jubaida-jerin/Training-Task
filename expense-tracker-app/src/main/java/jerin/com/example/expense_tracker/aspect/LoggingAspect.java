package jerin.com.example.expense_tracker.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before("execution(* jerin.com.example.expense_tracker.controller..*(..))")
    public void logBeforeControllerMethods(JoinPoint joinPoint) {
        logger.info("Entering method: {} with arguments: {}",
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs());
    }

    @AfterReturning(
            pointcut = "execution(* jerin.com.example.expense_tracker.controller..*(..))",
            returning = "result")
    public void logAfterControllerMethods(JoinPoint joinPoint, Object result) {
        logger.info("Exiting method: {} with result: {}",
                joinPoint.getSignature().toShortString(),
                result);
    }

    @AfterThrowing(
            pointcut = "execution(* jerin.com.example.expense_tracker..*(..))",
            throwing = "exception")
    public void logExceptions(JoinPoint joinPoint, Exception exception) {
        logger.error("Exception in method: {} with message: {}",
                joinPoint.getSignature().toShortString(),
                exception.getMessage());
    }
}