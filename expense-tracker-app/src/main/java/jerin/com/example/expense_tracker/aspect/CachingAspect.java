package jerin.com.example.expense_tracker.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class CachingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("@annotation(cacheable)")
    public Object cacheableAdvice(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
        String cacheName = cacheable.value()[0];
        String key = generateKey(joinPoint);
        logger.debug("Checking cache '{}' for key: {}", cacheName, key);


        Object result = joinPoint.proceed();
        logger.debug("Caching result in '{}' with key: {}", cacheName, key);
        return result;
    }

    @Around("@annotation(cacheEvict)")
    public Object cacheEvictAdvice(ProceedingJoinPoint joinPoint, CacheEvict cacheEvict) throws Throwable {
        String cacheName = cacheEvict.value()[0];
        logger.debug("Evicting cache '{}'", cacheName);


        Object result = joinPoint.proceed();
        return result;
    }

    private String generateKey(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        return methodName + Arrays.deepHashCode(args);
    }
}