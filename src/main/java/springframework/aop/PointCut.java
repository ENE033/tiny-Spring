package springframework.aop;


/**
 * 切点，用于获取MethodMatcher和ClassFilter两个类
 */
public interface PointCut {
    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();
}
