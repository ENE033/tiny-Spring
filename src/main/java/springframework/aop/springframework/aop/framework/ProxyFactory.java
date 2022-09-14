package springframework.aop.springframework.aop.framework;

import springframework.aop.springframework.aop.AdvisedSupport;

public class ProxyFactory extends AdvisedSupport {


    public Object getProxy() {
        return createAopProxy().getProxy();
    }

    private AopProxy createAopProxy() {
        if (isProxyTargetClass()) {
            return new CglibAopProxy(this);
        }
        return new JdkDynamicAopProxy(this);
    }

}
