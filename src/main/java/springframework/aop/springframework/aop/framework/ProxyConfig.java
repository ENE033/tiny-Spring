package springframework.aop.springframework.aop.framework;

public class ProxyConfig {

    //是否直接代理目标类(true)，还是代理指定的接口(false)
    private boolean ProxyTargetClass = false;

    public boolean isProxyTargetClass() {
        return ProxyTargetClass;
    }

    public void setProxyTargetClass(boolean proxyTargetClass) {
        ProxyTargetClass = proxyTargetClass;
    }
}
