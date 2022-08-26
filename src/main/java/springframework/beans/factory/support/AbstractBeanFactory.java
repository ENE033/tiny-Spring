package springframework.beans.factory.support;

import org.springframework.lang.Nullable;
import springframework.beans.BeansException;
import springframework.beans.factory.BeanFactory;
import springframework.beans.factory.config.BeanDefinition;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {

    @Override
    public Object getBean(String beanName) throws BeansException {
        return doGetBean(beanName, Object.class);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> requireType) throws BeansException {
        return doGetBean(beanName, requireType);
    }

    public <T> T doGetBean(String beanName, Class<T> requireType) throws BeansException {
        Object sharedInstance = getSingleton(beanName);
        if (sharedInstance != null) {
            return requireType.cast(sharedInstance);
        }
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        return requireType.cast(createBean(beanName, beanDefinition));
    }


    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException;

}
