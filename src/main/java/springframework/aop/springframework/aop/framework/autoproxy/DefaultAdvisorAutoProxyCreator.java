package springframework.aop.springframework.aop.framework.autoproxy;

import springframework.aop.aopalliance.aop.Advice;
import springframework.aop.springframework.aop.framework.ProxyFactory;
import springframework.aop.springframework.aop.*;
import springframework.aop.springframework.aop.framework.ProxyProcessorSupport;
import springframework.beans.BeansException;
import springframework.beans.factory.BeanFactory;
import springframework.beans.factory.BeanFactoryAware;
import springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import springframework.beans.factory.support.DefaultListableBeanFactory;
import springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultAdvisorAutoProxyCreator extends ProxyProcessorSupport implements BeanFactoryAware, SmartInstantiationAwareBeanPostProcessor {

    DefaultListableBeanFactory beanFactory;
    private final Map<Object, Object> earlyProxyReferences = new ConcurrentHashMap<>(8);

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
        if (bean != null) {
            Object cacheKey = getCacheKey(bean.getClass(), beanName);
            if (this.earlyProxyReferences.remove(cacheKey) != bean) {
                return wrapIfNecessary(bean, beanName);
            }
        }
        return bean;
    }

    private Object wrapIfNecessary(Object bean, String beanName) {
        Class<?> beanClass = this.beanFactory.getBeanDefinition(beanName).getBeanClass();
        if (isInfrastructureClass(beanClass)) {
            return bean;
        }
        Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(beanClass, beanName);
        if (specificInterceptors.length == 0) {
            return bean;
        }
        return createProxy(beanClass, beanName, specificInterceptors, new TargetSource(bean));
    }

    protected Object createProxy(Class<?> beanClass, String beanName, Object[] specificInterceptors, TargetSource targetSource) {
        Advisor[] advisors = buildAdvisors(beanName, specificInterceptors);
        ProxyFactory proxyFactory = new ProxyFactory();
        evaluateProxyInterfaces(beanClass, proxyFactory);
        proxyFactory.setTargetSource(targetSource);
        proxyFactory.addAdvisors(advisors);
        return proxyFactory.getProxy();
    }

    public Advisor[] buildAdvisors(String beanName, Object[] specificInterceptors) {
        List<Object> allInterceptors = new ArrayList<>();
        if (specificInterceptors.length > 0) {
            allInterceptors.addAll(Arrays.asList(specificInterceptors));
        }
        Advisor[] advisors = new Advisor[allInterceptors.size()];
        for (int i = 0; i < allInterceptors.size(); i++) {
            advisors[i] = wrap(allInterceptors.get(i));
        }
        return advisors;
    }

    private Advisor wrap(Object adviceObject) {
        if (adviceObject instanceof Advisor) {
            return (Advisor) adviceObject;
        }
        throw new BeansException("Advice object [" + adviceObject + "] is neither a supported subinterface of " +
                "[aopalliance.aop.Advice] nor an [springframework.aop.Advisor]");
    }


    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass) || Pointcut.class.isAssignableFrom(beanClass) || Advisor.class.isAssignableFrom(beanClass);
    }

    public Object[] getAdvicesAndAdvisorsForBean(Class<?> targetClass, String beanName) {
        List<Advisor> eligibleAdvisors = findEligibleAdvisors(targetClass, beanName);
        return eligibleAdvisors.toArray();
    }

    private List<Advisor> findEligibleAdvisors(Class<?> targetClass, String beanName) {
        List<Advisor> candidateAdvisors = findCandidateAdvisors();
        return findAdvisorsThatCanApply(candidateAdvisors, targetClass, beanName);
    }

    private List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> targetClass, String beanName) {
        List<Advisor> eligibleAdvisors = new ArrayList<>();
        if (candidateAdvisors.isEmpty()) {
            return eligibleAdvisors;
        }
        for (Advisor candidateAdvisor : candidateAdvisors) {
            if (candidateAdvisor instanceof PointcutAdvisor) {
                if (canApply(candidateAdvisor, targetClass)) {
                    eligibleAdvisors.add(candidateAdvisor);
                }
            }
        }
        return eligibleAdvisors;
    }

    private boolean canApply(Advisor advisor, Class<?> targetClass) {
        if (advisor instanceof PointcutAdvisor) {
            Pointcut pointcut = ((PointcutAdvisor) advisor).getPointcut();
            if (!pointcut.getClassFilter().matches(targetClass)) {
                return false;
            }
            MethodMatcher methodMatcher = pointcut.getMethodMatcher();
            Set<Class<?>> classes = new LinkedHashSet<>();
            if (!Proxy.isProxyClass(targetClass)) {
                classes.add(ClassUtils.getUserClass(targetClass));
            }
            classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
            for (Class<?> clazz : classes) {
                for (Method declaredMethod : clazz.getDeclaredMethods()) {
                    if (methodMatcher.matches(declaredMethod, targetClass)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    /**
     * 获取所有Advisor类的Bean集合
     *
     * @return
     */
    public List<Advisor> findCandidateAdvisors() {
        return new ArrayList<>(beanFactory.getBeansOfType(Advisor.class).values());
    }


    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        //return this.beanFactory.getSingleton(beanName);
        return null;
    }

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) {
        Object cacheKey = getCacheKey(bean.getClass(), beanName);
        this.earlyProxyReferences.put(cacheKey, bean);
        return wrapIfNecessary(bean, beanName);
    }


    private Object getCacheKey(Class<?> clazz, String beanName) {
        return beanName.isEmpty() ? clazz : beanName;
    }


}
