package springframework.aop.springframework.aop;

import java.lang.reflect.Method;

/**
 * 在返回之后，通知只在正常方法返回时调用，而不是在抛出异常时调用。这样的通知可以看到返回值，但不能更改它。
 */
public interface AfterReturningAdvice extends AfterAdvice {
    void afterReturning(Object returnValue, Method method, Object[] args, Object target);
}
