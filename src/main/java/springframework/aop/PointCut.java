package springframework.aop;


/**
 * 切点，组合MethodMatcher和ClassFilter两个类
 */
public interface PointCut {
    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();
}
