package springframework.beans.factory.config;

import springframework.beans.PropertyValues;
import springframework.beans.BeansException;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    default boolean postProcessAfterInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return true;
    }

    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }


    default PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
            throws BeansException {
        return pvs;
    }

    default PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName)
            throws BeansException {
        return pvs;
    }

}
