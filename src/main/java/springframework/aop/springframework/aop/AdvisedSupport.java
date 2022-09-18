package springframework.aop.springframework.aop;

import springframework.aop.springframework.aop.Pointcut;
import springframework.aop.springframework.aop.PointcutAdvisor;
import org.springframework.util.Assert;
import springframework.aop.aopalliance.aop.Advice;
import springframework.aop.aopalliance.intercept.MethodInterceptor;
import springframework.aop.springframework.aop.framework.ProxyConfig;
import springframework.aop.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import springframework.beans.BeansException;
import springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AOP 代理配置管理器
 */
public class AdvisedSupport extends ProxyConfig implements Advised {

    //被代理的对象
    private TargetSource targetSource;

    private final List<Advisor> advisors = new ArrayList<>();

    private final List<Class<?>> interfaces = new ArrayList<>();

    //方法匹配器
    private MethodMatcher methodMatcher;


    public void addAdvisors(Advisor[] advisors) {
        addAdvisors(Arrays.asList(advisors));
    }

    public void addAdvisors(List<Advisor> advisorList) {
        this.advisors.addAll(advisorList);
    }

    public List<Advisor> getAdvisors() {
        return advisors;
    }

    public void setInterfaces(Class<?>... interfaces) {
        this.interfaces.clear();
        for (Class<?> ifc : interfaces) {
            addInterface(ifc);
        }
    }

    public void addInterface(Class<?> intf) {
        if (!intf.isInterface()) {
            throw new BeansException("[" + intf.getName() + "] is not an interface");
        }
        if (!this.interfaces.contains(intf)) {
            this.interfaces.add(intf);
        }
    }

    public List<Class<?>> getInterfaces() {
        return interfaces;
    }


    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }


    public boolean isInterfaceProxied(Class<?> intf) {
        for (Class<?> anInterface : interfaces) {
            if (intf.isAssignableFrom(anInterface)) {
                return true;
            }
        }
        return false;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) {
        List<Object> methodInterceptors = new ArrayList<>();
        for (Advisor advisor : advisors) {
            PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
            Pointcut pointcut = pointcutAdvisor.getPointcut();
            if (!pointcut.getClassFilter().matches(targetClass)) {
                continue;
            }
            if (!pointcut.getMethodMatcher().matches(method, targetClass)) {
                continue;
            }
            Advice advice = advisor.getAdvice();
            if (advice instanceof MethodInterceptor) {
                methodInterceptors.add(advice);
            } else {
                if (advice instanceof MethodBeforeAdvice) {
                    methodInterceptors.add(new MethodBeforeAdviceInterceptor((MethodBeforeAdvice) advice));
                }
            }
        }
        return methodInterceptors;
    }

}
