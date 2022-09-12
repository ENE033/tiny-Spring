package springframework.beans.factory.support;

import springframework.beans.BeansException;
import springframework.beans.ClassUtil;
import springframework.beans.factory.FactoryBean;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.config.BeanPostProcessor;
import springframework.beans.factory.config.ConfigurableBeanFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {

    //bean后置增强器列表
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    private final ClassLoader classLoader = ClassUtil.getDefaultClassLoader();

    @Override
    public Object getBean(String beanName) throws BeansException {
        return doGetBean(beanName, Object.class, null);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> requireType) throws BeansException {
        return doGetBean(beanName, requireType, null);
    }

    @Override
    public Object getBean(String beanName, Object... args) throws BeansException {
        return doGetBean(beanName, Object.class, args);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> requireType, Object... args) throws BeansException {
        return doGetBean(beanName, requireType, args);
    }

    public <T> T doGetBean(String beanName, Class<T> requireType, Object[] args) throws BeansException {
        Object sharedInstance = getSingleton(beanName);
        T castedInstance;
        try {
            if (sharedInstance == null) {
                BeanDefinition beanDefinition = getBeanDefinition(beanName);
                Object bean = createBean(beanName, beanDefinition, args);
                Object objectForBeanInstance = getObjectForBeanInstance(bean, beanName);
                castedInstance = requireType.cast(objectForBeanInstance);
            } else {
                Object objectForBeanInstance = getObjectForBeanInstance(sharedInstance, beanName);
                castedInstance = requireType.cast(objectForBeanInstance);
            }
        } catch (ClassCastException e) {
            throw new BeansException(" Class cast failed ：", e);
        }
        if (castedInstance == null) {
            throw new BeansException(" Instance is null ：" + beanName);
        }
        return castedInstance;
    }


    private Object getObjectForBeanInstance(Object beanInstance, String beanName) {
        if (!(beanInstance instanceof FactoryBean)) {
            return beanInstance;
        }
        Object object = getCachedObjectForFactoryBean(beanName);
        if (object == null) {
            FactoryBean<?> factoryBean = (FactoryBean<?>) beanInstance;
            object = getObjectFromFactoryBean(factoryBean, beanName);
        }
        return object;
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }


    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException;


}
