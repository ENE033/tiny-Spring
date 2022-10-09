package springframework.aop.springframework.aop.framework.adapter;

import springframework.aop.springframework.aop.BeforeAdvice;
import springframework.aop.springframework.aop.MethodBeforeAdvice;
import springframework.aop.aopalliance.intercept.MethodInterceptor;
import springframework.aop.aopalliance.intercept.MethodInvocation;

/**
 * 包装MethodBeforeAdvice的拦截器。
 * 由AOP框架内部使用;应用程序开发人员不应该需要直接使用这个类。
 */
public class MethodBeforeAdviceInterceptor implements MethodInterceptor, BeforeAdvice {

    private MethodBeforeAdvice methodBeforeAdvice;

    public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
        this.methodBeforeAdvice = advice;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        methodBeforeAdvice.before(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return invocation.proceed();
    }

    public void setMethodBeforeAdvice(MethodBeforeAdvice advice) {
        this.methodBeforeAdvice = advice;
    }
}
