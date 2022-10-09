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
import springframework.beans.BeansException;
import springframework.util.ClassUtils;

/**
 * pointcut实现，它使用AspectJ编织器来计算切入点表达式。
 * 切入点表达式值是一个AspectJ表达式。这可以引用其他切入点并使用组合和其他操作。
 * 很自然，因为这是由Spring AOP基于代理的模型处理的，所以只支持方法执行切入点。
 */
public class AspectJExpressionPointcut implements Pointcut, ClassFilter, MethodMatcher {

    // 支持的原语集合
    private final static Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<>();

    // 类加载器
    private final ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

    // 添加EXECUTION原语
    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
    }

    // 切点表达式
    private PointcutExpression pointcutExpression;

    public AspectJExpressionPointcut() {
    }

    public AspectJExpressionPointcut(String expression) {
        pointcutExpression = buildPointcutExpression(expression);
    }

    // 设置切点
    public void setExpression(String expression) {
        pointcutExpression = buildPointcutExpression(expression);
    }

    // 解析切点
    private PointcutExpression buildPointcutExpression(String expression) {
        PointcutParser parser = initializePointcutParser();
        return parser.parsePointcutExpression(expression);
    }

    // 获取切点解析器
    private PointcutParser initializePointcutParser() {
        return PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
                SUPPORTED_PRIMITIVES, classLoader);
    }

    // 确定此切入点是否可以匹配给定类中的连接点
    @Override
    public boolean matches(Class<?> clazz) {
        return pointcutExpression.couldMatchJoinPointsInType(clazz);
    }

    // 确定此切入点是否与给定方法的执行相匹配
    @Override
    public boolean matches(Method method, Class<?> targetClass) throws BeansException {
        if (ClassUtils.isCglibProxyClass(targetClass)) {
            targetClass = targetClass.getSuperclass();
        }
        Method declaredMethod;
        // 匹配方法，如果方法存在就算匹配成功
        // 如果捕获了方法不存在异常则说明匹配不成功
        try {
            declaredMethod = targetClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
//            throw new BeansException(" No such method ：" + method.getName());
            return false;
        }
        return pointcutExpression.matchesMethodExecution(declaredMethod).alwaysMatches();
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
