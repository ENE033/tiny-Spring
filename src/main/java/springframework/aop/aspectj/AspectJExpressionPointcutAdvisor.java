package springframework.aop.aspectj;

import org.aopalliance.aop.Advice;
import springframework.aop.PointCut;
import springframework.aop.PointcutAdvisor;
import springframework.aop.aspectj.AspectJExpressionPointcut;

public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {

    private AspectJExpressionPointcut pointcut;

    private Advice advice;

    private String expression;

    @Override
    public Advice getAdvice() {
        return advice;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    @Override
    public PointCut getPointcut() {
        if (null == pointcut) {
            return new AspectJExpressionPointcut(expression);
        }
        return pointcut;
    }

    public void setPointcut(AspectJExpressionPointcut pointcut) {
        this.pointcut = pointcut;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
