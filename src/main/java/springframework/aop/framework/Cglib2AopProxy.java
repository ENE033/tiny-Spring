package springframework.aop.framework;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import springframework.aop.AdvisedSupport;
import springframework.aop.ReflectiveMethodInvocation;

import java.lang.reflect.Method;

/**
 * Cglib动态代理
 */
public class Cglib2AopProxy implements AopProxy {

    private final AdvisedSupport advisedSupport;

    public Cglib2AopProxy(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(advisedSupport.getTargetSource().getTargetClass());
        enhancer.setInterfaces(advisedSupport.getTargetSource().getTargetClass().getInterfaces());
        enhancer.setCallback(new DynamicAdvisedInterceptor(advisedSupport));
        return enhancer.create();
    }


    /**
     * Cglib方法拦截器
     */
    private static class DynamicAdvisedInterceptor implements MethodInterceptor {

        private final AdvisedSupport advisedSupport;

        public DynamicAdvisedInterceptor(AdvisedSupport advisedSupport) {
            this.advisedSupport = advisedSupport;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            CglibMethodInvocation cglibMethodInvocation = new CglibMethodInvocation(advisedSupport.getTargetSource().getTarget(), method, args, proxy);
            if (advisedSupport.getMethodMatcher().matches(method, advisedSupport.getTargetSource().getTargetClass())) {
                return advisedSupport.getMethodInterceptor().invoke(cglibMethodInvocation);
            }
            return cglibMethodInvocation.proceed();
        }
    }

    /**
     * Cglib切点
     */
    private static class CglibMethodInvocation extends ReflectiveMethodInvocation {

        MethodProxy methodProxy;

        public CglibMethodInvocation(Object target, Method method, Object[] arguments, MethodProxy methodProxy) {
            super(target, method, arguments);
            this.methodProxy = methodProxy;
        }

        @Override
        public Object proceed() throws Throwable {
            return methodProxy.invoke(this.getThis(), this.getArguments());
        }
    }

}
