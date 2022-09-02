package springframework.beans.factory.config;

import springframework.beans.BeansException;

/**
 * 提供了修改新实例化 Bean 对象的扩展点
 * <p>
 * 为什么使用default而不直接用抽象类？
 * 一个类可以实现多个接口，但是只能继承一个类
 * 所以在接口中使用default，可以让实现类选择性的重写自己需要的方法，而不需要重写全部方法
 */
public interface BeanPostProcessor {

    /**
     * 在 Bean 对象执行初始化方法之前，执行此方法
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 在 Bean 对象执行初始化方法之后，执行此方法
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


}
