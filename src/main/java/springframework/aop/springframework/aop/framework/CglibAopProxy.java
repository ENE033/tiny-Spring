package springframework.aop.springframework.aop.framework;

import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import springframework.aop.springframework.aop.AdvisedSupport;
import springframework.aop.springframework.aop.framework.support.AopUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Cglib动态代理
 */
public class CglibAopProxy implements AopProxy {

    private final AdvisedSupport advisedSupport;

    public CglibAopProxy(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(advisedSupport.getTargetSource().getTargetClass());
        enhancer.setInterfaces(AopUtils.completeProxiedInterfaces(advisedSupport));
//        enhancer.setCallbackFilter(new ProxyCallbackFilter(advisedSupport));
        enhancer.setCallback(new DynamicAdvisedInterceptor(advisedSupport));
//        System.out.println(enhancer.createClass());
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
            Object target = advisedSupport.getTargetSource().getTarget();
            List<Object> chain = advisedSupport.getInterceptorsAndDynamicInterceptionAdvice(method, obj.getClass());
            CglibMethodInvocation cglibMethodInvocation = new CglibMethodInvocation(target, method, args, proxy, chain);
            return cglibMethodInvocation.proceed();
        }
    }

    /**
     * Cglib切点
     */
    private static class CglibMethodInvocation extends ReflectiveMethodInvocation {

        MethodProxy methodProxy;

        public CglibMethodInvocation(Object target, Method method, Object[] arguments, MethodProxy methodProxy, List<Object> chain) {
            super(target, method, arguments, chain);
            this.methodProxy = methodProxy;
        }

        @Override
        public Object proceed() throws Throwable {
            return super.proceed();
        }
    }


    private static class ProxyCallbackFilter implements CallbackFilter {

        private final AdvisedSupport advisedSupport;

        public ProxyCallbackFilter(AdvisedSupport advisedSupport) {
            this.advisedSupport = advisedSupport;
        }

        @Override
        public int accept(Method method) {
            Object target = advisedSupport.getTargetSource().getTarget();
            Class<?> targetClass = advisedSupport.getTargetSource().getTargetClass();
            List<Object> chain = advisedSupport.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
            return 0;
        }
    }

}
