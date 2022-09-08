package springframework.aop.springframework.aop.aspectj;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import springframework.aop.springframework.aop.ClassFilter;
import springframework.aop.springframework.aop.MethodMatcher;
import springframework.aop.springframework.aop.Pointcut;
import springframework.beans.ClassUtil;

public class AspectJExpressionPointcut implements Pointcut, ClassFilter, MethodMatcher {

    //支持的原语集合
    private final static Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<>();

    private final ClassLoader classLoader = ClassUtil.getDefaultClassLoader();

    //添加EXECUTION原语
    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
    }

    private PointcutExpression pointcutExpression;

    public AspectJExpressionPointcut() {
    }

    //设置切点
    public void setExpression(String expression) {
        pointcutExpression = buildPointcutExpression(expression);
    }

    public AspectJExpressionPointcut(String expression) {
        pointcutExpression = buildPointcutExpression(expression);
    }

    //解析切点
    private PointcutExpression buildPointcutExpression(String expression) {
        PointcutParser parser = initializePointcutParser();
        return parser.parsePointcutExpression(expression);
    }

    //获取切点解析器
    private PointcutParser initializePointcutParser() {
        return PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
                SUPPORTED_PRIMITIVES, classLoader);
    }

    //确定此切入点是否可以匹配给定类中的连接点
    @Override
    public boolean matches(Class<?> clazz) {
        return pointcutExpression.couldMatchJoinPointsInType(clazz);
    }

    //确定此切入点是否与给定方法的执行相匹配
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return pointcutExpression.matchesMethodExecution(method).alwaysMatches();
    }

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }
}
