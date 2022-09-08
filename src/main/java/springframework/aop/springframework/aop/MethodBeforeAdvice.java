package springframework.aop.springframework.aop;

import java.lang.reflect.Method;

public interface MethodBeforeAdvice extends BeforeAdvice {
    void before(Object target, Method method, Object[] args);
}
