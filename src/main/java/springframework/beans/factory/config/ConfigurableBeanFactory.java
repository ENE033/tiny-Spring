package springframework.beans.factory.config;

import springframework.beans.factory.HierarchicalBeanFactory;

/**
 * 可获取 BeanPostProcessor、BeanClassLoader等的一个配置化接口
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

    /**
     * 标准单例范围的范围标识符：“单例”。
     */
    String SCOPE_SINGLETON = "singleton";

    /**
     * 标准原型范围的范围标识符：“原型”。
     */
    String SCOPE_PROTOTYPE = "prototype";

}