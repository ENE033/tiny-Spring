package springframework.beans.factory.support;

import springframework.beans.BeansException;
import springframework.beans.factory.PropertyPlaceholderConfigurer;
import springframework.util.ClassUtils;
import springframework.beans.factory.FactoryBean;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.config.BeanPostProcessor;
import springframework.beans.factory.config.ConfigurableBeanFactory;
import springframework.util.StringValueResolver;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {

    //bean后置增强器列表
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    private final ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

    private final List<StringValueResolver> embeddedValueResolvers = new ArrayList<>();

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
        Object beanInstance;
        if (sharedInstance != null) {
            beanInstance = getObjectForBeanInstance(sharedInstance, beanName);
        } else {
            BeanDefinition beanDefinition = getBeanDefinition(beanName);
            if (beanDefinition.isSingleton()) {
                sharedInstance = getSingleton(beanName, () -> {
                    try {
                        return createBean(beanName, beanDefinition, args);
                    } catch (Exception e) {
                        destroySingleton(beanName);
                        throw e;
                    }
                });
                beanInstance = getObjectForBeanInstance(sharedInstance, beanName);
            } else if (beanDefinition.isPrototype()) {
                Object prototypeInstance;
                prototypeInstance = createBean(beanName, beanDefinition, args);
                beanInstance = getObjectForBeanInstance(prototypeInstance, beanName);
            } else {
                throw new BeansException(" Unknown scope '" + beanDefinition.getScope() + "' of bean ：" + beanName);
            }
        }
        return requireType.cast(beanInstance);
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

    @Override
    public void addEmbeddedValueResolver(StringValueResolver valueResolver) {
        embeddedValueResolvers.add(valueResolver);
    }

    @Override
    public String resolveEmbeddedValue(String value) {
        String result = value;
        for (StringValueResolver embeddedValueResolver : embeddedValueResolvers) {
            result = embeddedValueResolver.resolveStringValue(result);
        }
        return result;
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
