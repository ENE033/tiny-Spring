package springframework.aop.framework.autoproxy;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import springframework.aop.*;
import springframework.aop.aspectj.AspectJExpressionPointcut;
import springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import springframework.aop.framework.ProxyFactory;
import springframework.beans.BeansException;
import springframework.beans.factory.BeanFactory;
import springframework.beans.factory.BeanFactoryAware;
import springframework.beans.factory.config.ConfigurableBeanFactory;
import springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.InvocationTargetException;
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
        return bean;
    }

    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass) || PointCut.class.isAssignableFrom(beanClass) || Advisor.class.isAssignableFrom(beanClass);
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (isInfrastructureClass(beanClass)) {
            return null;
        }
        Collection<AspectJExpressionPointcutAdvisor> advisors = beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();
        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
            ClassFilter classFilter = advisor.getPointcut().getClassFilter();
            if (!classFilter.matches(beanClass)) {
                continue;
            }
            AdvisedSupport advisedSupport = new AdvisedSupport();
//            Object bean = beanFactory.getBean(beanName, beanClass);
            TargetSource targetSource;
            try {
                targetSource = new TargetSource(beanClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                throw new BeansException(" targetSource filled in failed ", e);
            }
            advisedSupport.setTargetSource(targetSource);
            advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
            advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
//            advisedSupport.setMethodMatcher((AspectJExpressionPointcut) advisor.getPointcut());
            advisedSupport.setProxyTargetClass(false);
            return new ProxyFactory(advisedSupport).getProxy();
        }
        return null;
    }
}
