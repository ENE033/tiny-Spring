package springframework.aop.springframework.aop.framework;

import springframework.aop.springframework.aop.AdvisedSupport;
import springframework.aop.springframework.aop.TargetSource;

import java.lang.reflect.Proxy;

/**
 * AOP代理工厂的编程使用，而不是通过bean工厂中的声明性设置。
 * 该类提供了在自定义用户代码中获取和配置AOP代理实例的简单方法。
 */
public class ProxyFactory extends AdvisedSupport {

    /**
     * 根据该工厂中的设置创建一个新的代理。
     * 可重复调用。如果我们添加或删除了接口，效果会有所不同。可以添加和删除拦截器
     *
     * @return
     */
    public Object getProxy() {
        return createAopProxy().getProxy();
    }

    private AopProxy createAopProxy() {
        if (!isProxyTargetClass()) {
            return new JdkDynamicAopProxy(this);
        } else {
            TargetSource targetSource = getTargetSource();
            Class<?> targetClass = targetSource.getTargetClass();
            if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
                return new JdkDynamicAopProxy(this);
            }
            return new CglibAopProxy(this);
        }
    }

}
