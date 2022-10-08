package springframework.aop.aopalliance.intercept;

import java.lang.reflect.Method;

/**
 * 方法调用的描述，在方法调用时提供给拦截器。方法调用是一个连接点，可以被方法拦截器拦截。
 */
public interface MethodInvocation extends Invocation {
    // 获取正在被调用的方法。这个方法是Joinpoint.getStaticPart()方法的友好实现(结果相同)
    Method getMethod();
}
