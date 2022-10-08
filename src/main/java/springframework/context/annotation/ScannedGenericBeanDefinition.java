package springframework.context.annotation;

import springframework.beans.PropertyValues;
import springframework.beans.factory.support.GenericBeanDefinition;


/**
 * 源码中：
 * 继承 GenericBeanDefinition，实现 AnnotatedBeanDefinition 接口，多了一个 AnnotationMetadata 注解类元信息对象
 * 例如通过 @Component 注解定义的 Bean 会解析成该对象
 */

/**
 * 本项目中仅用于标识是@Component 定义的beanDefinition
 */
public class ScannedGenericBeanDefinition extends GenericBeanDefinition {

    public ScannedGenericBeanDefinition(Class<?> beanClass) {
        super(beanClass);
    }

    public ScannedGenericBeanDefinition(Class<?> beanClass, PropertyValues propertyValues) {
        super(beanClass, propertyValues);
    }
}
