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

/**
 * 基于当前BeanFactory中的所有候选切面创建AOP代理的BeanPostProcessor实现。
 */
public class DefaultAdvisorAutoProxyCreator extends ProxyProcessorSupport implements BeanFactoryAware, SmartInstantiationAwareBeanPostProcessor {

    // 通过BeanFactoryAware感知beanFactory
    DefaultListableBeanFactory beanFactory;
    // 用于缓存被代理过的bean
    private final Map<Object, Object> earlyProxyReferences = new ConcurrentHashMap<>(8);

    // 工厂感知
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * bean后置增强，用于创建需要被代理的对象的代理对象
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean != null) {
            // 获取cacheKey
            Object cacheKey = getCacheKey(bean.getClass(), beanName);
            // 缓存中取出cacheKey的值，检查cacheKey是否已经被代理过
            if (this.earlyProxyReferences.remove(cacheKey) != bean) {
                // 如果没有被代理过，则创建该对象的代理对象
                return wrapIfNecessary(bean, beanName);
            }
        }
        return bean;
    }

    /**
     * 判断bean是否需要被代理并决定是否创建代理对象
     *
     * @param bean
     * @param beanName
     * @return
     */
    private Object wrapIfNecessary(Object bean, String beanName) {
        // 获取bean的Class对象
        Class<?> beanClass = this.beanFactory.getBeanDefinition(beanName).getBeanClass();
        // 检查该bean的类是否基建类：Advice、PointCut、Advisor
        // 如果是则不代理，直接返回
        if (isInfrastructureClass(beanClass)) {
            return bean;
        }
        // 获取该类为切入点的所有切面
        Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(beanClass, beanName);
        // 如果没有符合条件的切面，直接返回
        if (specificInterceptors.length == 0) {
            return bean;
        }
        // 创建该bean的代理对象
        return createProxy(beanClass, beanName, specificInterceptors, new TargetSource(bean));
    }

    /**
     * 创建指定bean的代理对象
     *
     * @param beanClass            bean的Class对象
     * @param beanName             bean的id
     * @param specificInterceptors 能够拦截该bean的拦截器
     * @param targetSource         封装的bean对象
     * @return
     */
    protected Object createProxy(Class<?> beanClass, String beanName, Object[] specificInterceptors, TargetSource targetSource) {
        // 创建一个代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        // 在源码中：
        // 这个方法不是直接调用的，而是先调用shouldProxyTargetClass方法先判断
        // 即如果用户在xml文件或者在注解中将preserveTargetClass设置为true的话
        // 则不需要在使用这个方法去评估代理接口还是代理类了，而是直接代理类，使用cglib方式进行代理
        // 如果shouldProxyTargetClass的结果为false，才调用这个方法
        // 评估是代理接口还是代理类
        evaluateProxyInterfaces(beanClass, proxyFactory);
        // 注入封装的代理对象
        proxyFactory.setTargetSource(targetSource);
        // 获取指定拦截器的所有切面
        Advisor[] advisors = buildAdvisors(beanName, specificInterceptors);
        // 注入切面
        proxyFactory.addAdvisors(advisors);
        // 代理工厂创建代理对象
        return proxyFactory.getProxy();
    }

    /**
     * 将specificInterceptors中的增强或者切面全部转换为切面
     *
     * @param beanName
     * @param specificInterceptors 切面或者增强
     * @return
     */
    public Advisor[] buildAdvisors(String beanName, Object[] specificInterceptors) {
        // 在源码中：
        // 除了给定的拦截器，还有公共的拦截器，所以将数组转换为了List
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

    /**
     * 在源码中：
     * 该方法在AdvisorAdapterRegistry中定义，在DefaultAdvisorAdapterRegistry中实现
     *
     * @param adviceObject
     * @return
     */
    private Advisor wrap(Object adviceObject) {
        // 判断这个对象是否一个切面
        if (adviceObject instanceof Advisor) {
            return (Advisor) adviceObject;
        }
        // 在源码中：
        // 如果这个对象不是一个切面，而是一个增强的话
        // 会返回一个注入此增强的DefaultPointcutAdvisor
        throw new BeansException("Advice object [" + adviceObject + "] is neither a supported subinterface of " +
                "[aopalliance.aop.Advice] nor an [springframework.aop.Advisor]");
    }

    /**
     * 判断基建接口是否是beanClass的超接口
     *
     * @param beanClass
     * @return
     */
    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass) || Pointcut.class.isAssignableFrom(beanClass) || Advisor.class.isAssignableFrom(beanClass);
    }

    /**
     * 找到目标类的切面和增强
     *
     * @param targetClass
     * @param beanName
     * @return
     */
    public Object[] getAdvicesAndAdvisorsForBean(Class<?> targetClass, String beanName) {
        List<Advisor> eligibleAdvisors = findEligibleAdvisors(targetClass, beanName);
        return eligibleAdvisors.toArray();
    }

    private List<Advisor> findEligibleAdvisors(Class<?> targetClass, String beanName) {
        // 获取容器中所有切面的集合
        List<Advisor> candidateAdvisors = findCandidateAdvisors();
        // 筛选目标类的切面
        return findAdvisorsThatCanApply(candidateAdvisors, targetClass, beanName);
    }

    /**
     * 筛选目标类的切面
     *
     * @param candidateAdvisors 候选的切面
     * @param targetClass       目标类
     * @param beanName
     * @return 目标类的切面
     */
    private List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> targetClass, String beanName) {
        List<Advisor> eligibleAdvisors = new ArrayList<>();
        if (candidateAdvisors.isEmpty()) {
            return eligibleAdvisors;
        }
        for (Advisor candidateAdvisor : candidateAdvisors) {
            // 如果能目标类的切面，则加入到eligibleAdvisors
            if (canApply(candidateAdvisor, targetClass)) {
                eligibleAdvisors.add(candidateAdvisor);
            }
        }
        return eligibleAdvisors;
    }

    /**
     * 判断该切面是否是目标类的切面
     *
     * @param advisor
     * @param targetClass
     * @return
     */
    private boolean canApply(Advisor advisor, Class<?> targetClass) {
        if (advisor instanceof PointcutAdvisor) {
            // 获取切面的切入点
            Pointcut pointcut = ((PointcutAdvisor) advisor).getPointcut();
            // 判断该类是否切入点
            if (!pointcut.getClassFilter().matches(targetClass)) {
                return false;
            }
            // 获取切点的方法匹配器
            MethodMatcher methodMatcher = pointcut.getMethodMatcher();
            Set<Class<?>> classes = new LinkedHashSet<>();
            // 判断targetClass是否一个jdk动态代理类
            if (!Proxy.isProxyClass(targetClass)) {
                // 将targetClass加入classes
                // 如果targetClass是cglib代理对象则要加入其父类
                classes.add(ClassUtils.getUserClass(targetClass));
            }
            // 加入targetClass及其父类的所有接口
            classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
            // 如果匹配到该类及其父类的某个方法是切点，则返回true
            for (Class<?> clazz : classes) {
                for (Method declaredMethod : clazz.getDeclaredMethods()) {
                    if (methodMatcher.matches(declaredMethod, targetClass)) {
                        return true;
                    }
                }
            }
            // 如果匹配不到则返回false
            return false;
        }
        // 它没有切入点，所以假设它适用。
        return true;
    }

    /**
     * 获取容器中的所有切面的集合
     *
     * @return
     */
    public List<Advisor> findCandidateAdvisors() {
        return new ArrayList<>(beanFactory.getBeansOfType(Advisor.class).values());
    }


    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    /**
     * 提供给三级缓存的getObject方法调用的getEarlyBeanReference调用
     *
     * @param bean
     * @param beanName
     * @return
     */
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
