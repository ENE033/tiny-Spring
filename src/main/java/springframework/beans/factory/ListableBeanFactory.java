package springframework.beans.factory;


import springframework.beans.BeansException;

import java.util.Map;

/**
 * BeanFactory 接口的扩展，由可以枚举其所有 bean 实例的 bean 工厂实现，而不是按照客户的要求逐个尝试按名称查找 bean。
 * 预加载所有 bean 定义的 BeanFactory 实现（例如基于 XML 的工厂）可以实现此接口。
 */
public interface ListableBeanFactory extends BeanFactory {
    /**
     * 实例化所有type类型的bean并返回
     *
     * @param <T>
     * @param type bean的类型
     * @return map，键为beanName，值为bean
     * @throws BeansException
     */
    <T> Map<String, T> getBeansOfType(Class<T> type);

    /**
     * 返回注册表中所有的bean名称
     *
     * @return
     */
    String[] getBeanDefinitionNames();
}
