package springframework.beans.factory.support;

import springframework.beans.factory.support.InstantiationStrategy;
import springframework.beans.BeansException;
import springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    //创建bean的策略
    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = doCreateBean(beanName, beanDefinition, args);
        addSingleton(beanName, bean);
        return bean;
    }

    protected Object doCreateBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        System.out.println(instantiationStrategy.getClass());
        if (args == null) {
            return instantiationStrategy.instantiate(beanDefinition, beanName);
        } else {
            Constructor<?>[] constructors = beanDefinition.getBeanClass().getConstructors();
            int argsSize = args.length;
            Constructor<?> ctorToUse = null;
            int fixLength = 0;
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameters().length == argsSize) {
                    ctorToUse = constructor;
                    fixLength++;
                }
            }
            if (fixLength == 0) {
                throw new BeansException("Could not resolve matching constructor on bean class [" + beanDefinition.getBeanClass().getName() + "]");
            } else if (fixLength > 1) {
                throw new BeansException("Ambiguous constructor matches found on bean class [" + beanDefinition.getBeanClass().getName() + "]");
            }
            return instantiationStrategy.instantiate(beanDefinition, beanName, ctorToUse, args);
        }
    }

//    public InstantiationStrategy getInstantiationStrategy() {
//        return instantiationStrategy;
//    }
//
//    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
//        this.instantiationStrategy = instantiationStrategy;
//    }

    public void setSimpleStrategy(boolean isSimple) {
        if (isSimple) {
            this.instantiationStrategy = new SimpleInstantiationStrategy();
        } else {
            this.instantiationStrategy = new CglibSubclassingInstantiationStrategy();
        }
    }

}
