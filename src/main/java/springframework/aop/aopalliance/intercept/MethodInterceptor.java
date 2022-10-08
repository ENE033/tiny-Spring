package springframework.aop.aopalliance.intercept;

/**
 * 拦截器在调用接口的过程中调用目标。
 * 这些嵌套在目标的“顶部”。用户应该实现invoke(MethodInvocation)方法来修改原始行为。
 */
public interface MethodInterceptor extends Interceptor {
    // 实现这个方法，以便在调用前后执行额外的处理。正确的实现肯定会调用Joinpoint.proceed()。
    Object invoke(MethodInvocation invocation) throws Throwable;
}
