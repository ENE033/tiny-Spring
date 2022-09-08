package springframework.aop.springframework.aop;

import springframework.aop.aopalliance.intercept.MethodInterceptor;

/**
 * AOP 代理配置管理器
 */
public class AdvisedSupport {
    //是否直接代理目标类(true)，还是代理指定的接口(false)
    private boolean ProxyTargetClass = false;
    //被代理的对象
    private TargetSource targetSource;
    //方法拦截器
    private MethodInterceptor methodInterceptor;
    //方法匹配器
    private MethodMatcher methodMatcher;

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }

    public boolean isProxyTargetClass() {
        return ProxyTargetClass;
    }

    public void setProxyTargetClass(boolean proxyTargetClass) {
        ProxyTargetClass = proxyTargetClass;
    }
}
