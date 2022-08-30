package springframework.beans.factory.support;

import springframework.beans.BeansException;
import springframework.beans.factory.BeanFactory;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.config.BeanPostProcessor;
import springframework.beans.factory.config.ConfigurableBeanFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {


    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

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
                castedInstance = requireType.cast(createBean(beanName, beanDefinition, args));
            } else {
                castedInstance = requireType.cast(sharedInstance);
            }
        } catch (ClassCastException e) {
            throw new BeansException(" Class cast failed ", e);
        }
        if (castedInstance == null) {
            throw new BeansException(" castedInstance is null ");
        }
        return castedInstance;
    }


    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException;

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }
}
