package org.javig.micro_adversarial_search.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggerAspect {

    private static final Logger logger = LogManager.getLogger(LoggerAspect.class);

    @Around("execution(* org.javig.micro_adversarial_search.service.SearchService.calculaJugada(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.debug("Propiedades mundo: {}", joinPoint.getArgs()[0]);
        return joinPoint.proceed();
    }

}
