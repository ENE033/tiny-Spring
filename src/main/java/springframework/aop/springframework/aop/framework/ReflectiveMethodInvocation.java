package springframework.aop.springframework.aop.framework;

import springframework.aop.aopalliance.intercept.MethodInterceptor;
import springframework.aop.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 可被方法拦截器拦截的连接点，可以使用反射执行目标对象原方法
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

    @Override
    public Object proceed() throws Throwable {
        if (currentInterceptorIndex == this.interceptors.size() - 1) {
            return method.invoke(target, arguments);
        }
        Object interceptor = this.interceptors.get(++currentInterceptorIndex);
        return ((MethodInterceptor) interceptor).invoke(this);
    }
}
