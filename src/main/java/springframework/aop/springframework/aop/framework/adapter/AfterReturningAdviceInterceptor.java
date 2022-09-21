package springframework.aop.springframework.aop.framework.adapter;

import springframework.aop.aopalliance.intercept.MethodInterceptor;
import springframework.aop.aopalliance.intercept.MethodInvocation;
import springframework.aop.springframework.aop.AfterAdvice;
import springframework.aop.springframework.aop.AfterReturningAdvice;

public class AfterReturningAdviceInterceptor implements MethodInterceptor, AfterAdvice {

    private final AfterReturningAdvice advice;

    public AfterReturningAdviceInterceptor(AfterReturningAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object retVal = invocation.proceed();
        advice.afterReturning(retVal, invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return retVal;
    }
}
