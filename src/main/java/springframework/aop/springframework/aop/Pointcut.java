package springframework.aop.springframework.aop;


/**
 * 切点，组合MethodMatcher和ClassFilter两个类
 */
public interface Pointcut {
    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();
}
