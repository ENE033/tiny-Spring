package springframework.aop.framework.adapter;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import springframework.aop.MethodBeforeAdvice;

public class MethodBeforeAdviceInterceptor implements MethodInterceptor {

    private MethodBeforeAdvice advice;

    public MethodBeforeAdviceInterceptor() {
    }

    public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        advice.before(invocation.getThis(), invocation.getMethod(), invocation.getArguments());
        return invocation.proceed();
    }

    public void setAdvice(MethodBeforeAdvice advice) {
        this.advice = advice;
    }
}
