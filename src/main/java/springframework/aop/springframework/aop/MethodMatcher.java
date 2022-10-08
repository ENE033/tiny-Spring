package springframework.aop.springframework.aop;

import java.lang.reflect.Method;

/**
 * 方法匹配器，用于匹配表达式范围内的目标方法
 * <p>
 * 切入点的一部分:检查目标方法是否有资格获得通知。
 * MethodMatcher可以是静态的，也可以是运行时的(动态的)。
 * 静态匹配涉及方法和(可能的)方法属性。
 * 动态匹配还使特定调用的参数可用，并使应用于连接点的运行之前的通知的任何影响可用。
 */
public interface MethodMatcher {
    /**
     * 执行静态检查给定方法是否匹配。
     *
     * @param method
     * @param targetClass
     * @return
     */
    boolean matches(Method method, Class<?> targetClass);

}
