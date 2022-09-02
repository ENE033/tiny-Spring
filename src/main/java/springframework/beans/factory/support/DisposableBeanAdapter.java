package springframework.beans.factory.support;

import springframework.beans.BeanUtils;
import springframework.beans.BeansException;
import springframework.beans.factory.DisposableBean;
import springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * DisposableBean适配器，为了使没有实现DisposableBean但是存在destroy-method方法的bean能够注册到disposableBeans中
 * 这样做可以符合要求的bean直接注册，而不需要关心具体是哪种实现方式
 * 具体的实现方式交给本类DisposableBeanAdapter
 * <p>
 * Note:
 * 为什么DisposableBean需要disposableBeans，而InitializingBean不需要？
 * InitializingBean和有init-Method的bean会在创建时执行完bean中的初始化方法
 * 而DisposableBean和有destroy-Method的bean要再虚拟机结束的时候才会执行bean中的销毁方法
 * 所以后者的bean需要一个disposableBeans的注册表来缓存
 * <p>
 * 为什么DisposableBean需要DisposableBeanAdapter适配器，而InitializingBean则不需要适配器呢？
 * 为什么呢？？
 */
public class DisposableBeanAdapter implements DisposableBean {

    private final Object bean;
    private final String beanName;
    private final String destroyMethodName;
    private final BeanDefinition beanDefinition;

    public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
        this.bean = bean;
        this.beanName = beanName;
        this.destroyMethodName = beanDefinition.getDestroyMethodName();
        this.beanDefinition = beanDefinition;
    }

    /**
     * 执行disposableBean的destroy或xml中的destroy-method
     *
     * @throws BeansException
     */
    @Override
    public void destroy() throws BeansException {
        //如果该bean实现了DisposableBean，则执行destroy方法
        if (bean instanceof DisposableBean) {
            try {
                ((DisposableBean) bean).destroy();
            } catch (BeansException e) {
                throw new BeansException(" Method destroy execution failed ：" + beanName, e);
            }
        }
        //执行xml中的destroy-method，但是不能执行两次destroy方法，需要判断
        if (destroyMethodName != null && !destroyMethodName.isEmpty() && !((bean instanceof DisposableBean) && "destroy".equals(this.destroyMethodName))) {
            Method destroyMethod = BeanUtils.findDeclaredMethod(bean.getClass(), destroyMethodName);
            if (destroyMethod == null) {
                throw new BeansException(" No such method ：" + destroyMethodName);
            }
            try {
                destroyMethod.setAccessible(true);
                destroyMethod.invoke(bean);
            } catch (IllegalAccessException e) {
                throw new BeansException(" Method can not access ：" + destroyMethod, e);
            } catch (InvocationTargetException e) {
                throw new BeansException(" Unknown error currently ：" + destroyMethod, e);
            }
        }

    }
}
