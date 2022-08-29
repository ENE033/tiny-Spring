package springframework.beans.factory;

import springframework.beans.BeansException;
import springframework.beans.factory.config.AutowireCapableBeanFactory;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.config.BeanPostProcessor;
import springframework.beans.factory.config.ConfigurableBeanFactory;


/**
 * 大多数可列出的bean工厂要实现的配置接口。
 * 除了 ConfigurableBeanFactory 之外，它还提供了分析和修改 bean 定义以及预实例化单例的工具
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

    /**
     * 根据beanName获取beanDefinition
     *
     * @param beanName
     * @return
     * @throws BeansException
     */
    BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    /**
     * 注册bean后置增强器
     *
     * @param beanPostProcessor
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    /**
     * 提前实例化单例bean
     *
     * @throws BeansException
     */
    void preInstantiateSingletons() throws BeansException;

}