package springframework.aop.springframework.aop.framework;

import springframework.aop.aopalliance.intercept.MethodInvocation;
import springframework.aop.springframework.aop.Advised;
import springframework.aop.springframework.aop.AdvisedSupport;
import springframework.aop.springframework.aop.TargetSource;
import springframework.aop.springframework.aop.framework.support.AopUtils;
import springframework.util.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * jdk原生动态代理
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    //AOP 代理配置管理器
    private final AdvisedSupport advisedSupport;
    private final Class<?>[] proxiedInterfaces;

    public JdkDynamicAopProxy(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
        this.proxiedInterfaces = AopUtils.completeProxiedInterfaces(this.advisedSupport);

    }

    //执行方法拦截器或调用原方法
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object retVal;
        TargetSource targetSource = this.advisedSupport.getTargetSource();
        Object target = targetSource.getTarget();
        Class<?> targetClass = target.getClass();
        List<Object> chain = advisedSupport.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
        if (!chain.isEmpty()) {
            MethodInvocation methodInvocation = new ReflectiveMethodInvocation(target, method, args, chain);
            retVal = methodInvocation.proceed();
        } else {
            retVal = method.invoke(target, args);
        }
        return retVal;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(
                ClassUtils.getDefaultClassLoader(),
                this.proxiedInterfaces,
                this);
    }


}
