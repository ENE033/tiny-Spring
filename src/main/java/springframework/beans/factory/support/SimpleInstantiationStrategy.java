package springframework.beans.factory.support;

import springframework.beans.BeansException;
import springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

public class SimpleInstantiationStrategy implements InstantiationStrategy {
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor<?>[] constructors, Object... args) throws BeansException {
        int argsSize = args.length;
        int fixSize = 0;
        Constructor<?> ctorToUse = null;
        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length != argsSize) {
                continue;
            }
            boolean flag = true;
            for (int i = 0; i < argsSize; i++) {
                if (args[i] == null) {
                    continue;
                }
                if (args[i].getClass() != parameterTypes[i]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                ctorToUse = constructor;
                fixSize++;
            }
        }
        if (fixSize == 0) {
            throw new BeansException("Could not resolve matching constructor on bean class [" + beanDefinition.getBeanClass().getName() + "]");
        } else if (fixSize > 1) {
            throw new BeansException("Ambiguous constructor matches found on bean class [" + beanDefinition.getBeanClass().getName() + "]");
        }
        Object bean = null;
        try {
            bean = ctorToUse.newInstance(args);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName) throws BeansException {
        Object bean;
        try {
            bean = beanDefinition.getBeanClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeansException("Instantiation of bean failed", e);
        } catch (NoSuchMethodException e) {
            throw new BeansException("No such method", e);
        }
        return bean;
    }
}
