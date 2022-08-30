package springframework.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import springframework.beans.PropertyValue;
import springframework.beans.PropertyValues;
import springframework.beans.factory.DisposableBean;
import springframework.beans.factory.InitializingBean;
import springframework.beans.factory.config.AutowireCapableBeanFactory;
import springframework.beans.factory.config.BeanPostProcessor;
import springframework.beans.factory.config.BeanReference;
import springframework.beans.BeansException;
import springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        Object bean;
        try {
            //创建bean
            bean = doCreateBean(beanName, beanDefinition, args);
            //给bean赋值
            applyPropertyValues(beanName, bean, beanDefinition);
            //初始化bean
            bean = initializeBean(beanName, bean, beanDefinition);
            //注册单例bean
            addSingleton(beanName, bean);

            registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

        } catch (BeansException e) {
            throw new BeansException(" Bean register fail : " + beanName, e);
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
                    try {
                        constructor.newInstance(args);
                    } catch (Exception e) {
                        continue;
                    }
                    ctorToUse = constructor;
                    fixLength++;
                    if (fixLength > 1) {
                        break;
                    }
                }
            }


            /**
             * old version1
             */
            //for (Constructor<?> constructor : constructors) {
            //    if (constructor.getParameters().length == argsSize) {
            //        ctorToUse = constructor;
            //        fixLength++;
            //    }
            //}

            if (fixLength == 0) {
                throw new BeansException(" Could not resolve matching constructor on bean class [" + beanDefinition.getBeanClass().getName() + "]");
            } else if (fixLength > 1) {
                throw new BeansException(" Ambiguous constructor matches found on bean class [" + beanDefinition.getBeanClass().getName() + "]");
            }
            return instantiationStrategy.instantiate(beanDefinition, beanName, ctorToUse, args);
        }
    }


    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        if (bean instanceof DisposableBean || !beanDefinition.getDestroyMethodName().isEmpty()) {
            registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));
        }
    }


    /**
     * bean属性填充
     *
     * @param beanName
     * @param bean
     * @param beanDefinition
     */
    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) throws BeansException {
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        if (propertyValues == null) {
            throw new BeansException(" PropertyValues are null ");
        }
        for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
            String name = propertyValue.getName();
            Object value = propertyValue.getValue();
            if (value instanceof BeanReference) {
                BeanReference beanReference = (BeanReference) value;
                value = getBean(beanReference.getBeanName());
            }
            try {
                //用BeanUtil进行属性注入
                BeanUtil.setFieldValue(bean, name, value);
            } catch (Exception e) {
                throw new BeansException(" Property injection failed ", e);
            }

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
    }

    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        Object wrappedBean;
        try {
            //执行 BeanPostProcessor Before 处理
            wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);
            //激活初始化方法
            invokeInitMethods(beanName, wrappedBean, beanDefinition);
            //执行 BeanPostProcessor After 处理
            wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        } catch (BeansException e) {
            throw new BeansException(" bean initialization failed ", e);
        }
        return wrappedBean;
    }

    private void invokeInitMethods(String beanName, Object wrappedBean, BeanDefinition beanDefinition) {
        if (wrappedBean instanceof InitializingBean) {
            try {
                ((InitializingBean) wrappedBean).afterPropertiesSet();
            } catch (Exception e) {
                throw new BeansException("", e);
            }
        }

        //配置init-method
        String initMethodName = beanDefinition.getInitMethodName();
        //避免二次执行销毁操作
        if (!initMethodName.isEmpty()) {
            Method initMethod;
            try {
                initMethod = beanDefinition.getBeanClass().getMethod(initMethodName);
            } catch (NoSuchMethodException e) {
                throw new BeansException(" No such method ：" + initMethodName, e);
            }
            try {
//                initMethod.setAccessible(true);
                initMethod.invoke(wrappedBean);
            } catch (IllegalAccessException e) {
                throw new BeansException(" Method can not access ：" + initMethodName, e);
            } catch (InvocationTargetException e) {
                throw new BeansException(" Unknown error currently ：" + initMethodName, e);
            }
        }
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
