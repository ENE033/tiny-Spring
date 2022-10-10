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

    /**
     * 创建代理对象
     * 在源码中：
     * 这个方法在AopProxyFactory中定义，在DefaultAopProxyFactory中实现
     *
     * @return
     */
    private AopProxy createAopProxy() {
        // 先判断是否代理类，如果不是则代理接口，则使用jdk动态代理
        if (!isProxyTargetClass()) {
            return new JdkDynamicAopProxy(this);
        } else {
            TargetSource targetSource = getTargetSource();
            Class<?> targetClass = targetSource.getTargetClass();
            // 判断目标类是否接口，是否已经被动态代理过了，在源码中还需要判断是否lambda类
            // 如果是则使用jdk动态代理
            if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
                return new JdkDynamicAopProxy(this);
            }
            return new CglibAopProxy(this);
        }
    }

}
