package springframework.aop.springframework.aop;

import springframework.aop.aopalliance.aop.Advice;
import springframework.aop.aopalliance.intercept.MethodInterceptor;
import springframework.aop.springframework.aop.framework.ProxyConfig;
import springframework.aop.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor;
import springframework.aop.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import springframework.beans.BeansException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AOP 代理配置管理器
 */
public class AdvisedSupport extends ProxyConfig implements Advised {

    // 被代理的对象
    private TargetSource targetSource;

    // 切面列表
    private final List<Advisor> advisors = new ArrayList<>();

    // 接口列表
    private final List<Class<?>> interfaces = new ArrayList<>();

    // 方法匹配器
    private MethodMatcher methodMatcher;

    // 批量增加切面
    public void addAdvisors(Advisor[] advisors) {
        addAdvisors(Arrays.asList(advisors));
    }

    public void addAdvisors(List<Advisor> advisorList) {
        this.advisors.addAll(advisorList);
    }

    public List<Advisor> getAdvisors() {
        return advisors;
    }

    // 设置接口
    public void setInterfaces(Class<?>... interfaces) {
        this.interfaces.clear();
        for (Class<?> ifc : interfaces) {
            addInterface(ifc);
        }
    }

    // 增加接口
    public void addInterface(Class<?> intf) {
        if (!intf.isInterface()) {
            throw new BeansException("[" + intf.getName() + "] is not an interface");
        }
        if (!this.interfaces.contains(intf)) {
            this.interfaces.add(intf);
        }
    }

    // 获取所有接口
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

    /**
     * 判断指定接口是否代理接口或者代理接口的父接口
     *
     * @param intf
     * @return
     */
    public boolean isInterfaceProxied(Class<?> intf) {
        for (Class<?> anInterface : interfaces) {
            if (intf.isAssignableFrom(anInterface)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 找到指定类的指定方法的所有拦截器
     *
     * @param method
     * @param targetClass
     * @return
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) {
        List<Object> methodInterceptors = new ArrayList<>();
        // 遍历所有切面
        for (Advisor advisor : advisors) {
            if (!(advisor instanceof PointcutAdvisor)) {
                continue;
            }
            PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
            // 获取切点
            Pointcut pointcut = pointcutAdvisor.getPointcut();
            // 匹配给定的类
            if (!pointcut.getClassFilter().matches(targetClass)) {
                continue;
            }
            // 匹配给定的方法
            if (!pointcut.getMethodMatcher().matches(method, targetClass)) {
                continue;
            }
            // 类和方法都能匹配成功，说明该切面能增强该方法
            Advice advice = advisor.getAdvice();
            // 判断该类是否MethodInterceptor的实现
            if (advice instanceof MethodInterceptor) {
                // 如果是则直接放入拦截器列表中
                methodInterceptors.add(advice);
            } else {
                // 如果不是
                // 那么则根据类型封装到MethodInterceptor的实现类中再加入到拦截器列表中
                if (advice instanceof MethodBeforeAdvice) {
                    methodInterceptors.add(new MethodBeforeAdviceInterceptor((MethodBeforeAdvice) advice));
                } else if (advice instanceof AfterReturningAdvice) {
                    methodInterceptors.add(new AfterReturningAdviceInterceptor((AfterReturningAdvice) advice));
                }
            }
        }
        return methodInterceptors;
    }

}
