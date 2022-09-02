package springframework.context.support;

import springframework.context.ConfigurableApplicationContext;
import springframework.beans.BeansException;
import springframework.beans.factory.config.BeanPostProcessor;
import springframework.context.ApplicationContextAware;

public class ApplicationContextAwareProcessor implements BeanPostProcessor {

    private final ConfigurableApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 重写初始化前执行的后置处理器
     * 让实现了ApplicationContextAware的bean能够感知所属的applicationContext
     * <p>
     * 不需要重写初始化后的后置处理器是因为BeanPostProcessor中使用了default关键字
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof ApplicationContextAware)) {
            return bean;
        }

        /**
         * 中间可以执行权限判断等操作
         */

        invokeAwareInterfaces(bean);
        return bean;
    }

    private void invokeAwareInterfaces(Object bean) {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
        }
    }

}
