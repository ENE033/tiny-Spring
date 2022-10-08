package springframework.aop.springframework.aop;

import java.util.List;

/**
 * 接口由类实现，这些类持有AOP代理工厂的配置。
 * 这个配置包括拦截器、其他增强、切面和代理接口。
 * 从Spring获得的任何AOP代理都可以转换到这个接口，从而允许操纵它的AOP通知。
 * <p>
 * AOP 代理工厂配置类接口。提供了操作和管理 Advice 和 Advisor 的能力。
 * 它的实现类 ProxyFactory 是 Spring AOP 主要用于创建 AOP 代理类的核心类。
 */
public interface Advised {

    boolean isProxyTargetClass();

    boolean isInterfaceProxied(Class<?> intf);

    void setTargetSource(TargetSource targetSource);

    TargetSource getTargetSource();

    void addAdvisors(Advisor[] advisors);

    void addAdvisors(List<Advisor> advisors);


}
