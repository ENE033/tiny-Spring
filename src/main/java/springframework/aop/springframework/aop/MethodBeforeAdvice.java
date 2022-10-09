package springframework.aop.springframework.aop;

import java.lang.reflect.Method;

/**
 * 在调用方法之前调用通知。这样的通知不能阻止方法调用的进行，除非它们抛出一个Throwable。
 */
public interface MethodBeforeAdvice extends BeforeAdvice {
    void before(Method method, Object[] args, Object target);
}
