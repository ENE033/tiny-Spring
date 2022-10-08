package springframework.beans.factory.support;

import springframework.beans.PropertyValues;
import springframework.beans.factory.config.BeanDefinition;


/**
 * 源码中：
 * 继承 AbstractBeanDefinition 抽象类，多了一个 parentName，表示有继承关系
 * 是一个标准 Bean 元信息对象，通过 XML 定义的 Bean 会解析成该对象
 */


/**
 * 本项目中仅用于标识是xml定义的beanDefinition
 */
public class GenericBeanDefinition extends BeanDefinition {

    public GenericBeanDefinition(Class<?> beanClass) {
        super(beanClass);
    }

    public GenericBeanDefinition(Class<?> beanClass, PropertyValues propertyValues) {
        super(beanClass, propertyValues);
    }
}
