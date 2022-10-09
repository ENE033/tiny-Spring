package springframework.aop.springframework.aop.framework;

import springframework.beans.factory.Aware;
import springframework.beans.factory.DisposableBean;
import springframework.beans.factory.InitializingBean;
import springframework.util.ClassUtils;

import java.util.Set;

public class ProxyProcessorSupport extends ProxyConfig {
    /**
     * 检查给定bean类上的接口，并将它们应用到ProxyFactory(如果合适的话)。
     * 调用isConfigurationCallbackInterface和isInternalLanguageInterface来筛选合理的代理接口，
     * 否则退回到目标类代理。
     *
     * @param beanClass
     * @param proxyFactory
     */
    protected void evaluateProxyInterfaces(Class<?> beanClass, ProxyFactory proxyFactory) {
        // 获取beanClass及其父类的所有接口
        Class<?>[] interfaces = ClassUtils.getAllInterfacesForClassAsSet(beanClass).toArray(new Class<?>[0]);
        boolean hasReasonableProxyInterface = false;
        for (Class<?> anInterface : interfaces) {
            if (!isConfigurationCallbackInterface(anInterface)
                    && !isInternalLanguageInterface(anInterface)
                    && anInterface.getMethods().length > 0) {
                hasReasonableProxyInterface = true;
                break;
            }
        }
        // 如果这个Class存在符合条件的接口且接口中有方法
        if (hasReasonableProxyInterface) {
            // 将该类及其父类的所有接口都加入到proxyFactory的interface集合中
            for (Class<?> anInterface : interfaces) {
                proxyFactory.addInterface(anInterface);
            }
        }
        // 否则将代理类，而不是代理接口，即CGlib代理bean
        else {
            proxyFactory.setProxyTargetClass(true);
        }
    }

    /**
     * 确定给定的接口是否只是一个容器的回调而不能被视为一个合理的代理接口。
     * 不能是Aware，InitializingBean，DisposableBean
     *
     * @param clazz
     * @return
     */
    private boolean isConfigurationCallbackInterface(Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (Aware.class.isAssignableFrom(anInterface)) {
                return false;
            }
        }
        return InitializingBean.class == clazz || DisposableBean.class == clazz;
    }

    /**
     * 确定给定的接口是否是内部语言接口而不能被视为合理的代理接口。
     *
     * @param clazz
     * @return
     */
    private boolean isInternalLanguageInterface(Class<?> clazz) {
        return clazz.getName().endsWith(".cglib.proxy.Factory");
    }

}
