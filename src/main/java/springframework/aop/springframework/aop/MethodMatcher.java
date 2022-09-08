package springframework.aop.springframework.aop;

import java.lang.reflect.Method;

/**
 * 方法匹配器，用于匹配表达式范围内的目标方法
 */
public interface MethodMatcher {

    boolean matches(Method method, Class<?> targetClass);

}
