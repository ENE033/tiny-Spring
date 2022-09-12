package springframework.context.annotation;

import springframework.beans.PropertyValues;
import springframework.beans.factory.support.GenericBeanDefinition;

public class ScannedGenericBeanDefinition extends GenericBeanDefinition {

    public ScannedGenericBeanDefinition(Class<?> beanClass) {
        super(beanClass);
    }

    public ScannedGenericBeanDefinition(Class<?> beanClass, PropertyValues propertyValues) {
        super(beanClass, propertyValues);
    }
}
