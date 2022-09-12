package springframework.beans.factory.support;

import springframework.beans.BeansException;
import springframework.beans.factory.config.BeanDefinition;

/**
 * 注册beanDefinition
 */
public interface BeanDefinitionRegistry {
    /**
     * 在注册表中注册 BeanDefinition
     *
     * @param beanName
     * @param beanDefinition
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    boolean containsBeanDefinition(String beanName);

    BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    String[] getBeanDefinitionNames();

    /**
     * 检查给定候选的 bean 名称，确定相应的 bean 定义是否需要注册或与现有定义冲突。
     *
     * @param beanName
     * @param beanDefinition
     * @return true:bean定义 可以按原样注册
     * false:因为指定名称存在现有的兼容 bean 定义而应该跳过它
     * @throws BeansException 找到了现有的、不兼容的 bean 定义
     */
    boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws BeansException;

    //确定给定的新 bean 定义是否与给定的现有 bean 定义兼容

    /**
     * 判断原有的bean定义与现有的bean定义是否兼容
     *
     * @param newDefinition
     * @param existingDefinition
     * @return true:兼容
     * false:不兼容
     */
    boolean isCompatible(BeanDefinition newDefinition, BeanDefinition existingDefinition);


    void mergeBeanDefinition(BeanDefinition newDefinition, BeanDefinition existingDefinition);

}
