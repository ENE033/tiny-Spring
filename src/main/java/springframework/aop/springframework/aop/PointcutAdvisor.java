package springframework.aop.springframework.aop;

/**
 * 所有由切入点驱动的切面的父接口。这几乎涵盖了所有的切面
 */
public interface PointcutAdvisor extends Advisor {
    // 获取驱动这个切面的切入点。
    Pointcut getPointcut();
}
