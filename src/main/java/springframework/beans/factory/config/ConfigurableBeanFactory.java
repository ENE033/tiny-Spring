package springframework.beans.factory.config;

import springframework.beans.factory.HierarchicalBeanFactory;
import springframework.util.StringValueResolver;

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

    /**
     * 注册bean后置增强器
     *
     * @param beanPostProcessor
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    /**
     * 执行disposableBeans中的bean的销毁方法
     */
    void destroySingletons();

    /**
     * 为嵌入值（例如注释属性）添加字符串解析器。
     *
     * @param valueResolver
     */
    void addEmbeddedValueResolver(StringValueResolver valueResolver);

    /**
     * 解析给定的嵌入值，例如注解属性。
     *
     * @param value
     * @return
     */
    String resolveEmbeddedValue(String value);

}
