package springframework.aop.springframework.aop.framework;

import springframework.aop.aopalliance.intercept.MethodInterceptor;
import springframework.aop.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Spring对AOP联盟方法调用接口的实现，实现了扩展的ProxyMethodInvocation接口。
 * 使用反射调用目标对象。重复调用proceed()
 * 注意:这个类被认为是内部的，不应该被直接访问。
 */
public class ReflectiveMethodInvocation implements MethodInvocation {

    private final Object target;
    private final Method method;
    private final Object[] arguments;
    private final List<Object> interceptors;
    private int currentInterceptorIndex = -1;

    public ReflectiveMethodInvocation(Object target, Method method, Object[] arguments, List<Object> chain) {
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.interceptors = chain;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public AccessibleObject getStaticPart() {
        return method;
    }

    public List<Object> getInterceptors() {
        return interceptors;
    }

    /**
     * 增强点执行增强方法和原方法
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object proceed() throws Throwable {
        // 如果已经将拦截器的增强逻辑执行完毕，则执行原方法
        if (currentInterceptorIndex == this.interceptors.size() - 1) {
            return method.invoke(target, arguments);
        }
        // 获取下一个拦截器
        Object interceptor = this.interceptors.get(++currentInterceptorIndex);
        // 执行拦截器的增强逻辑
        return ((MethodInterceptor) interceptor).invoke(this);
    }
}
