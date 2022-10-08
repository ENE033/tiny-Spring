package springframework.aop.springframework.aop;


/**
 * 核心Spring切入点抽象。
 * 切入点由一个类过滤器和一个方法匹配器组成
 * <p>
 * 选择一组相关连接点的模式，即可以认为连接点的集合，
 * Spring支持perl5正则表达式和AspectJ切入点模式，Spring默认使用AspectJ语法，
 * 在AOP中表示为“在哪里干的集合”；
 */
public interface Pointcut {
    // 返回这个切入点的ClassFilter。
    ClassFilter getClassFilter();

    // 返回这个切入点的MethodMatcher。
    MethodMatcher getMethodMatcher();
}
