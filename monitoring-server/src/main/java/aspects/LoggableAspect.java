package aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Аспект для логирования вызовов методов, аннотированных @Loggable.
 */
@Aspect
public class LoggableAspect {
    /**
     * Определение среза для методов, аннотированных @Loggable.
     */
    @Pointcut("within(@annotations.Loggable *) && execution(* * (..))")
    public void annotatedByLoggable(){}

    /**
     * Совет, который логирует время выполнения методов, аннотированных @Loggable.
     *
     * @param proceedingJoinPoint Объект ProceedingJoinPoint, представляющий выполнение метода.
     * @return Object Результат выполнения метода.
     * @throws Throwable В случае ошибки во время выполнения метода.
     */
    @Around("annotatedByLoggable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        System.out.println("Calling method " + proceedingJoinPoint.getSignature());
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis() - start;
        System.out.println("Execution of method " + proceedingJoinPoint.getSignature() +
                " finished. Execution time is " + end + " ms.");
        return result;
    }
}
