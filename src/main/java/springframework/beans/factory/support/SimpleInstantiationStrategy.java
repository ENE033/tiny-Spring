package springframework.beans.factory.support;

import springframework.beans.BeansException;
import springframework.beans.factory.BeanFactory;
import springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SimpleInstantiationStrategy implements InstantiationStrategy {
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor<?> ctor, Object... args) throws BeansException {
        Object bean;
        try {
            if (ctor == null || args == null) {
                bean = beanDefinition.getBeanClass().getDeclaredConstructor().newInstance();
            } else {
                bean = ctor.newInstance(args);
            }
        } catch (NoSuchMethodException e) {
            throw new BeansException(" No such method ：" + beanName, e);
        } catch (IllegalAccessException e) {
            throw new BeansException(" Method does not have access ：" + beanName, e);
        } catch (InstantiationException e) {
            throw new BeansException(" Class object cannot be instantiated ：" + beanName, e);
        } catch (InvocationTargetException e) {
            throw new BeansException(" Instantiation of bean failed ：" + beanName, e);
        }
        return bean;
    }

    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName) throws BeansException {
        return instantiate(beanDefinition, beanName, null, (Object) null);
    }

    protected Object instantiateWithMethodInjection(BeanDefinition beanDefinition, String beanName, BeanFactory owner, Constructor<?> ctor, Object... args) {
        throw new BeansException("Method Injection not supported in SimpleInstantiationStrategy");
    }

    protected Object instantiateWithMethodInjection(BeanDefinition beanDefinition, String beanName, BeanFactory owner) {
        return instantiateWithMethodInjection(beanDefinition, beanName, owner, null, (Object) null);
    }


}
