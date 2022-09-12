package springframework.beans.factory.support;

import springframework.beans.PropertyValues;
import springframework.beans.factory.config.BeanDefinition;

public class GenericBeanDefinition extends BeanDefinition {


    public GenericBeanDefinition(Class<?> beanClass) {
        super(beanClass);
    }

    public GenericBeanDefinition(Class<?> beanClass, PropertyValues propertyValues) {
        super(beanClass, propertyValues);
    }
}
