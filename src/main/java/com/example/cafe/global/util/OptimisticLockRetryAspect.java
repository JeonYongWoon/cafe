package com.example.cafe.global.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OptimisticLockRetryAspect {

    private static final int MAX_RETRIES = 15;

    @Around("@annotation(com.example.cafe.global.util.RetryOnCollision)")
    public Object retry(ProceedingJoinPoint joinPoint) throws Throwable {
        int attempt = 0;
        while (true) {
            try {
                return joinPoint.proceed();
            } catch (OptimisticLockingFailureException e) {
                attempt++;
                if (attempt >= MAX_RETRIES) {
                    throw e;
                }
                try {
                    long backoff = 30 + java.util.concurrent.ThreadLocalRandom.current().nextInt(40);
                    Thread.sleep(backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
    }
}
