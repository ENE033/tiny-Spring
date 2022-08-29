package springframework.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import springframework.beans.PropertyValue;
import springframework.beans.PropertyValues;
import springframework.beans.factory.config.AutowireCapableBeanFactory;
import springframework.beans.factory.config.BeanPostProcessor;
import springframework.beans.factory.config.BeanReference;
import springframework.beans.BeansException;
import springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    //创建bean的策略
    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

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

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = doCreateBean(beanName, beanDefinition, args);
        try {
            if (beanDefinition.getPropertyValues() != null) {
                applyPropertyValues(beanName, bean, beanDefinition);
            }
            addSingleton(beanName, bean);
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return bean;
    }

    protected Object doCreateBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
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
                throw new BeansException(" Could not resolve matching constructor on bean class [" + beanDefinition.getBeanClass().getName() + "]");
            } else if (fixLength > 1) {
                throw new BeansException(" Ambiguous constructor matches found on bean class [" + beanDefinition.getBeanClass().getName() + "]");
            }
            return instantiationStrategy.instantiate(beanDefinition, beanName, ctorToUse, args);
        }
    }

    /**
     * bean属性填充
     *
     * @param beanName
     * @param bean
     * @param beanDefinition
     */
    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        try {
            PropertyValues propertyValues = beanDefinition.getPropertyValues();
            if (propertyValues == null) {
                throw new BeansException(" No propertyValues to apply ");
            }
            for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                String name = propertyValue.getName();
                Object value = propertyValue.getValue();
                if (value instanceof BeanReference) {
                    BeanReference beanReference = (BeanReference) value;
                    value = getBean(beanReference.getBeanName());
                }
                //用BeanUtil进行属性注入
                BeanUtil.setFieldValue(bean, name, value);

                /**
                 * old version
                 */
                //                try {
                //                    Field field = bean.getClass().getDeclaredField(name);
                //                    field.setAccessible(true);
                //                    field.set(bean, value);
                //                } catch (NoSuchFieldException e) {
                //                    throw new BeansException(beanName + " No Such Field：" + name);
                //                } catch (IllegalAccessException e) {
                //                    e.printStackTrace();
                //                }
            }
        } catch (BeansException e) {
            throw new BeansException(" Error setting property values：" + beanName + " Cause: " + e.getMessage());
        }
    }


    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        //执行 BeanPostProcessor Before 处理
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);
        //激活初始化方法
        invokeInitMethods(beanName, wrappedBean, beanDefinition);
        //执行 BeanPostProcessor After 处理
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
        return wrappedBean;
    }

    private void invokeInitMethods(String beanName, Object wrappedBean, BeanDefinition beanDefinition) {

    }

    /**
     * 执行初始化前的后置处理器
     *
     * @param existingBean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object current = beanPostProcessor.postProcessBeforeInitialization(existingBean, beanName);
            if (current == null) {
                return result;
            } else {
                result = current;
            }
        }
        return result;
    }

    /**
     * 执行初始化后的后置处理器
     *
     * @param existingBean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object current = beanPostProcessor.postProcessAfterInitialization(existingBean, beanName);
            if (current == null) {
                return result;
            } else {
                result = current;
            }
        }
        return result;
    }
}
