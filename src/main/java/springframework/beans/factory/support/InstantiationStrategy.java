package springframework.beans.factory.support;


import springframework.beans.BeansException;
import springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;

public interface InstantiationStrategy {

    Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor<?> ctor, Object... args) throws BeansException;

    Object instantiate(BeanDefinition beanDefinition, String beanName) throws BeansException;

}
