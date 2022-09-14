package springframework.aop.springframework.aop.framework.autoproxy;

import springframework.aop.aopalliance.aop.Advice;
import springframework.aop.aopalliance.intercept.MethodInterceptor;
import springframework.aop.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import springframework.aop.springframework.aop.framework.ProxyFactory;
import springframework.aop.springframework.aop.*;
import springframework.aop.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import springframework.beans.BeansException;
import springframework.beans.factory.BeanFactory;
import springframework.beans.factory.BeanFactoryAware;
import springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Collection;

public class DefaultAdvisorAutoProxyCreator implements BeanFactoryAware, InstantiationAwareBeanPostProcessor {

    DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = this.beanFactory.getBeanDefinition(beanName).getBeanClass();
        if (isInfrastructureClass(beanClass)) {
            return bean;
        }
        Collection<AspectJExpressionPointcutAdvisor> advisors = beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();
        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
            ClassFilter classFilter = advisor.getPointcut().getClassFilter();
            if (!classFilter.matches(beanClass)) {
                continue;
            }
            ProxyFactory proxyFactory = new ProxyFactory();
//            Object bean = beanFactory.getBean(beanName, beanClass);
            TargetSource targetSource;
            try {
                targetSource = new TargetSource(bean);
            } catch (Exception e) {
                throw new BeansException(" targetSource filled in failed ", e);
            }
            proxyFactory.setTargetSource(targetSource);
            proxyFactory.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
            proxyFactory.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
//            advisedSupport.setMethodMatcher((AspectJExpressionPointcut) advisor.getPointcut());
            proxyFactory.setProxyTargetClass(false);
            return proxyFactory.getProxy();
        }
        return bean;
    }

    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass) || Pointcut.class.isAssignableFrom(beanClass) || Advisor.class.isAssignableFrom(beanClass);
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return this.beanFactory.getSingleton(beanName);
    }
}
