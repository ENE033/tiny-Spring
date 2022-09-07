package springframework.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import springframework.beans.BeanUtils;
import springframework.beans.PropertyValue;
import springframework.beans.PropertyValues;
import springframework.beans.factory.*;
import springframework.beans.factory.config.*;
import springframework.beans.BeansException;

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

    /**
     * 创建bean，赋值，初始化，注册到单例，注册销毁方法
     *
     * @param beanName
     * @param beanDefinition
     * @param args
     * @return
     * @throws BeansException
     */
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean;
        try {
            bean = resolveBeforeInstantiation(beanName, beanDefinition);
            if (bean != null) {
                return bean;
            }
            //创建bean
            bean = doCreateBean(beanName, beanDefinition, args);
            //给bean赋值
            applyPropertyValues(beanName, bean, beanDefinition);
            //初始化bean
            bean = initializeBean(beanName, bean, beanDefinition);
            //注册实现了DisposableBean或destroy-method不为空的bean, 等销毁时再执行销毁方法
            registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);
        } catch (BeansException e) {
            throw new BeansException(" Bean register fail : " + beanName, e);
        }
        //如果是个单例bean，注册singletonObjects中
        if (beanDefinition.isSingleton()) {
            registerSingleton(beanName, bean);
        } else if (!beanDefinition.isPrototype()) {
            //如果既不是单例又不是原型，暂且抛一个异常
            throw new BeansException(" Unknown scope '" + beanDefinition.getScope() + "' of bean ：" + beanName);
        }
        return bean;
    }

    protected Object resolveBeforeInstantiation(String beanName, BeanDefinition beanDefinition) {
        Object bean = applyBeanPostProcessorsBeforeInstantiation(beanDefinition.getBeanClass(), beanName);
        if (bean != null) {
            applyBeanPostProcessorsAfterInitialization(bean, beanName);
        }
        return bean;
    }

    protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                Object bean = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessBeforeInstantiation(beanClass, beanName);
                if (bean != null) {
                    return bean;
                }
            }
        }
        return null;
    }


    /**
     * 创建bean实例
     *
     * @param beanName
     * @param beanDefinition
     * @param args
     * @return
     * @throws BeansException
     */
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

    /**
     * 注册实现了DisposableBean的bean,等销毁时再执行销毁方法
     *
     * @param beanName
     * @param bean
     * @param beanDefinition
     */
    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        //非单例bean不注册不执行销毁方法
        if (!beanDefinition.isSingleton()) {
            return;
        }
        String destroyMethodName = beanDefinition.getDestroyMethodName();
        if (bean instanceof DisposableBean || (destroyMethodName != null && !destroyMethodName.isEmpty())) {
            //适配器，为了使不论是否实现了DisposableBean接口的bean都能被注册到disposableBeans中
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
//                Field field = beanDefinition.getBeanClass().getDeclaredField(name);
//                field.setAccessible(true);
//                field.set(bean, value);
            } catch (Exception e) {
                throw new BeansException(" Property injection failed ", e);
            }

            /**
             * old version
             */
            //try {
            //    Field field = bean.getClass().getDeclaredField(name);
            //    field.setAccessible(true);
            //    field.set(bean, value);
            //} catch (NoSuchFieldException e) {
            //    throw new BeansException(beanName + " No Such Field：" + name);
            //} catch (IllegalAccessException e) {
            //    e.printStackTrace();
            //}
        }
    }

    /**
     * 初始化bean
     *
     * @param beanName
     * @param bean
     * @param beanDefinition
     * @return
     */
    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {

        //调用感知方法
        invokeAwareMethods(beanName, bean);

        Object wrappedBean = bean;
        //执行 BeanPostProcessor Before 处理
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
        try {
            //激活初始化方法
            invokeInitMethods(beanName, wrappedBean, beanDefinition);
        } catch (BeansException e) {
            throw new BeansException(" Bean initialization failed ", e);
        }
        //执行 BeanPostProcessor After 处理
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        return wrappedBean;
    }

    private void invokeAwareMethods(String beanName, Object bean) {
        /**
         * 通过判断来进行通知实现相应接口的类，并执行感知方法
         */
        if (bean instanceof Aware) {
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware) bean).setBeanFactory(this);
            }
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
            if (bean instanceof BeanClassLoaderAware) {
                ((BeanClassLoaderAware) bean).setBeanClassLoader(getClassLoader());
            }
        }
    }

    /**
     * 执行初始化方法
     *
     * @param beanName
     * @param wrappedBean
     * @param beanDefinition
     */
    private void invokeInitMethods(String beanName, Object wrappedBean, BeanDefinition beanDefinition) {
        //如果bean实现了InitializingBean接口，则执行afterPropertiesSet方法
        if (wrappedBean instanceof InitializingBean) {
            try {
                //执行afterPropertiesSet方法
                ((InitializingBean) wrappedBean).afterPropertiesSet();
            } catch (Exception e) {
                throw new BeansException(" AfterPropertiesSet method on bean with name " + beanName + " threw an exception ", e);
            }
        }
        //获取init-method的方法名
        String initMethodName = beanDefinition.getInitMethodName();
        //避免二次执行销毁操作，afterPropertiesSet中可能实行销毁操作
        if (initMethodName != null && !initMethodName.isEmpty()) {
            Method initMethod;
//          initMethod = beanDefinition.getBeanClass().getDeclaredMethod(initMethodName);
            initMethod = BeanUtils.findDeclaredMethod(wrappedBean.getClass(), initMethodName);
            if (initMethod == null) {
                throw new BeansException(" No such method ：" + initMethodName);
            }
            try {
                //关闭可入性检查
                initMethod.setAccessible(true);
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
