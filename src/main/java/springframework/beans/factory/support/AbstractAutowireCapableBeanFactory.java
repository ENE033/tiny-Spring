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

    /**
     * 设置beanFactory的实例化策略
     *
     * @param isSimple
     */
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
        } catch (BeansException e) {
            throw new BeansException(" Bean register fail : " + beanName, e);
        }
        return bean;
    }

    /**
     * 填充bean
     *
     * @param beanName
     * @param bean
     * @param beanDefinition
     */
    protected void populateBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        //注入使用注解@Value和@Autowired的属性
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (InstantiationAwareBeanPostProcessor.class.isAssignableFrom(beanPostProcessor.getClass())) {
                ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessProperties(beanDefinition.getPropertyValues(), bean, beanName);
            }
        }
        //注入xml解析的属性，会覆盖掉注解注入的属性
        applyPropertyValues(beanName, bean, beanDefinition);
    }

    protected Object resolveBeforeInstantiation(String beanName, BeanDefinition beanDefinition) {
        Object bean = applyBeanPostProcessorsBeforeInstantiation(beanDefinition.getBeanClass(), beanName);
        if (bean != null) {
            bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        }
        return bean;
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

        //实例化bean
        Object bean = createBeanInstance(beanName, beanDefinition, args);

        boolean earlySingletonExposure = beanDefinition.isSingleton() && isSingletonCurrentlyInCreation(beanName);

        //如果所实例的bean是处于正在被创建状态的，则可能存在循环依赖
        if (earlySingletonExposure) {
            //加入三级缓存
            addSingletonFactory(beanName, () -> getEarlyBeanReference(bean, beanName));
        }
        Object exposedBean = bean;
        //填充bean
        populateBean(beanName, exposedBean, beanDefinition);

        //初始化bean
        exposedBean = initializeBean(beanName, exposedBean, beanDefinition);

        //尝试从二级缓存中获取对象或对象的代理对象
        if (earlySingletonExposure) {
            Object earlySingletonReference = getSingleton(beanName, false);
            if (earlySingletonReference != null) {
                //如果初始化之后的exposedBean跟未初始化的bean一样，则替换为二级缓存中的earlySingletonReference
                //比如循环依赖中，A中的B中的A是增强的代理对象，而原本的A则是没有增强的，因为单例A已经被增强过了且放入二级缓存了，不需要再增强
                //导致原本的A和初始化前后都是未被增强的普通对象，此时就应该将二级缓存中的代理对象赋值给原本的A
                if (exposedBean == bean) {
                    exposedBean = earlySingletonReference;
                }
            }
        }

        //注册实现了DisposableBean或destroy-method不为空的bean, 等销毁时再执行销毁方法
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

        return exposedBean;
    }

    /**
     * 三级缓存中的ObjectFactory的getObject方法中会调用此方法按需生成代理对象
     *
     * @param bean
     * @param beanName
     * @return
     */
    protected Object getEarlyBeanReference(Object bean, String beanName) {
        Object exposedBean = bean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (SmartInstantiationAwareBeanPostProcessor.class.isAssignableFrom(beanPostProcessor.getClass())) {
                exposedBean = ((SmartInstantiationAwareBeanPostProcessor) beanPostProcessor).getEarlyBeanReference(bean, beanName);
            }
        }
        return exposedBean;
    }

    protected Object createBeanInstance(String beanName, BeanDefinition beanDefinition, Object... args) {
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
    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) throws
            BeansException {
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        if (propertyValues == null) {
            throw new BeansException(" PropertyValues are null ");
        }
        for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
            String name = propertyValue.getName();
            Object value = propertyValue.getValue();
            Object resolvedValue = resolveValueIfNecessary(value);
            try {
                //用BeanUtil进行属性注入
                BeanUtil.setFieldValue(bean, name, resolvedValue);
            } catch (Exception e) {
                throw new BeansException(" Property injection failed ", e);
            }
        }
    }

    /**
     * 解析属性
     * 如果是TypedStringValue，则解析为字符串
     * 如果是BeanReference，则解析为bean对象
     *
     * @param originalValue
     * @return
     */
    private Object resolveValueIfNecessary(Object originalValue) {
        Object value = originalValue;
        if (value instanceof BeanReference) {
            BeanReference beanReference = (BeanReference) value;
            value = getBean(beanReference.getBeanName());
        } else if (value instanceof TypedStringValue) {
            TypedStringValue typedStringValue = (TypedStringValue) value;
            value = typedStringValue.getValue();
        }
        return value;
    }

    /**
     * 初始化bean
     *
     * @param beanName
     * @param bean
     * @param beanDefinition
     * @return
     */
    @Override
    public Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {

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

    /**
     * 通过判断来进行通知实现相应接口的类，并执行感知方法
     *
     * @param beanName
     * @param bean
     */
    private void invokeAwareMethods(String beanName, Object bean) {
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
     * 执行实例化前的后置处理器
     *
     * @param beanClass
     * @param beanName
     * @return
     */
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
     * 执行初始化前的后置处理器
     *
     * @param existingBean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws
            BeansException {
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
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws
            BeansException {
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