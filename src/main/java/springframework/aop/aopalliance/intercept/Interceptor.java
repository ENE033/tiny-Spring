package springframework.aop.aopalliance.intercept;

import springframework.aop.aopalliance.aop.Advice;

/**
 * 此接口表示通用拦截器。通用拦截器可以拦截在基础程序中发生的运行时事件。
 * 这些事件被连接点物化(具体化)。运行时连接点可以是调用、字段访问、异常……该接口不能直接使用。
 * 使用子接口来拦截特定的事件。
 */
public interface Interceptor extends Advice {
}
