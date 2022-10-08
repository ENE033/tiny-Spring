package springframework.aop.springframework.aop;

import springframework.aop.aopalliance.aop.Advice;

/**
 * 基本接口包含AOP通知(在连接点采取的动作)和决定通知适用性的过滤器(如切入点)。
 * 这个接口不是供Spring用户使用的，而是为了实现对不同类型通知的支持的通用性。
 * Spring AOP基于通过方法拦截传递的通知，与AOP联盟拦截API兼容。
 * Advisor接口允许支持不同类型的通知，例如通知之前和之后，这不需要使用拦截实现。
 * <p>
 * 可以理解为不完全的切面，只能获取到Advice增强，该接口不能获取到PointCut切点
 */
public interface Advisor {
    // 返回此切面的增强部分。一个增强可以是拦截器
    Advice getAdvice();
}
