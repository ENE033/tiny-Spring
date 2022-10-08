package springframework.aop.aopalliance.intercept;

import java.lang.reflect.AccessibleObject;


/**
 * 这个接口表示一个通用的运行时连接点(在AOP术语中)。
 * 运行时连接点是发生在静态连接点(即程序中的位置)上的事件。
 * 例如，调用是方法上的运行时连接点(静态连接点)。
 * 可以使用getStaticPart()方法通用地检索给定连接点的静态部分。
 * 在拦截框架的上下文中，运行时连接点是对可访问对象(方法、构造函数、字段)的访问的具体化，即连接点的静态部分。
 * 它被传递给安装在静态连接点上的拦截器。
 * <p>
 * 连接点，表示需要在程序中插入横切关注点的扩展点，
 * 连接点可能是类初始化、方法执行、方法调用、字段调用或处理异常等等，
 * Spring只支持方法执行作为连接点，在AOP中表示为“在哪里干”；
 * <p>
 * 允许作为切入点的资源，所有类的所有方法均可以作为连接点
 */
public interface Joinpoint {
    // 执行链中的下一个拦截器。
    Object proceed() throws Throwable;

    // 返回持有当前joinpoint的静态部分的对象。例如，调用的目标对象。
    Object getThis();

    // 返回这个连接点的静态部分。静态部分是一个可访问的对象，在其上安装了拦截器链。
    AccessibleObject getStaticPart();
}
