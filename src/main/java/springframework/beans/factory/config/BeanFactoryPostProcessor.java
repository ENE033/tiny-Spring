package springframework.beans.factory.config;

import springframework.beans.BeansException;
import springframework.beans.factory.ConfigurableListableBeanFactory;

/**
 * 这个接口是满足于在所有的 BeanDefinition 加载完成后，实例化 Bean 对象之前，提供修改 BeanDefinition 属性的机制
 */
public interface BeanFactoryPostProcessor {

    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
