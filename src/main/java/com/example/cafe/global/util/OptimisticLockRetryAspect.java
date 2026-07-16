package com.example.cafe.global.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Slf4j
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
                    log.error("동시성 충돌 재시도 횟수 초과 (최대: {}), 메서드: {}", MAX_RETRIES, joinPoint.getSignature().toShortString(), e);
                    throw e;
                }
                long backoff = 30 + java.util.concurrent.ThreadLocalRandom.current().nextInt(40);
                log.warn("동시성 충돌 발생 - 재시도 횟수: {}/{}, 메서드: {}, 백오프 대기: {}ms", 
                        attempt, MAX_RETRIES, joinPoint.getSignature().toShortString(), backoff);
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
    }
}
