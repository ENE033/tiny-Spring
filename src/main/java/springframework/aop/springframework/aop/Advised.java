package springframework.aop.springframework.aop;


public interface Advised {

    boolean isProxyTargetClass();

    boolean isInterfaceProxied(Class<?> intf);

    void setTargetSource(TargetSource targetSource);

    TargetSource getTargetSource();

}
