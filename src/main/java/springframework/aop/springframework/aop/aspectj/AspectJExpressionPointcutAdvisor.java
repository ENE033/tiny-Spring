package springframework.aop.springframework.aop.aspectj;

import springframework.aop.aopalliance.aop.Advice;
import springframework.aop.springframework.aop.Pointcut;
import springframework.aop.springframework.aop.PointcutAdvisor;

/**
 * 可以用于任何AspectJ切入点表达式的Spring AOP 切面。
 */
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {

    // 创建一个AspectJExpression切点
    private AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

    // 增强
    private Advice advice;

    // 表达式
    private String expression;

    // 获取增强
    @Override
    public Advice getAdvice() {
        return advice;
    }

    // 设置增强
    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    // 获取切点
    @Override
    public Pointcut getPointcut() {
        pointcut.setExpression(expression);
        return pointcut;
    }

    // 设置切点
    public void setPointcut(AspectJExpressionPointcut pointcut) {
        this.pointcut = pointcut;
    }

    // 获取表达式
    public String getExpression() {
        return expression;
    }

    // 设置表达式
    public void setExpression(String expression) {
        this.expression = expression;
    }
}
