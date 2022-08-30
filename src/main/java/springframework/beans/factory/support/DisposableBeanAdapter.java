package springframework.beans.factory.support;

import springframework.beans.BeansException;
import springframework.beans.factory.DisposableBean;
import springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DisposableBeanAdapter implements DisposableBean {

    private final Object bean;
    private final String beanName;
    private final String destroyMethodName;

    public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
        this.bean = bean;
        this.beanName = beanName;
        this.destroyMethodName = beanDefinition.getDestroyMethodName();
    }

    @Override
    public void destroy() throws BeansException {
        if (bean instanceof DisposableBean) {
            try {
                ((DisposableBean) bean).destroy();
            } catch (BeansException e) {
                throw new BeansException("", e);
            }
        }
        if (!destroyMethodName.isEmpty() && !(bean instanceof DisposableBean) && "destroy".equals(this.destroyMethodName)) {
            Method destroyMethod;
            try {
                destroyMethod = bean.getClass().getMethod(destroyMethodName);
            } catch (NoSuchMethodException e) {
                throw new BeansException(" No such method ：" + destroyMethodName, e);
            }
            try {
//                destroyMethod.setAccessible(true);
                destroyMethod.invoke(bean);
            } catch (IllegalAccessException e) {
                throw new BeansException(" Method can not access ：" + destroyMethod, e);
            } catch (InvocationTargetException e) {
                throw new BeansException(" Unknown error currently ：" + destroyMethod, e);
            }
        }

    }
}
