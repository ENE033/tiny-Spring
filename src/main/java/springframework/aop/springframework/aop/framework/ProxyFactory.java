package springframework.aop.springframework.aop.framework;

import springframework.aop.springframework.aop.AdvisedSupport;
import springframework.aop.springframework.aop.TargetSource;

import java.lang.reflect.Proxy;

public class ProxyFactory extends AdvisedSupport {


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
