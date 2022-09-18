package springframework.aop.springframework.aop.framework;

import springframework.beans.factory.DisposableBean;
import springframework.beans.factory.InitializingBean;
import springframework.util.ClassUtils;

import java.util.Set;

public class ProxyProcessorSupport extends ProxyConfig {

    protected void evaluateProxyInterfaces(Class<?> beanClass, ProxyFactory proxyFactory) {
        Class<?>[] interfaces = ClassUtils.getAllInterfacesForClassAsSet(beanClass).toArray(new Class<?>[0]);
        boolean hasReasonableProxyInterface = false;
        for (Class<?> anInterface : interfaces) {
            if (!isConfigurationCallbackInterface(anInterface) && !isInternalLanguageInterface(anInterface) && anInterface.getMethods().length > 0) {
                hasReasonableProxyInterface = true;
            }
            break;
        }
        if (hasReasonableProxyInterface) {
            for (Class<?> anInterface : interfaces) {
                proxyFactory.addInterface(anInterface);
            }
        } else {
            proxyFactory.setProxyTargetClass(true);
        }
    }

    private boolean isConfigurationCallbackInterface(Class<?> clazz) {
        return InitializingBean.class == clazz || DisposableBean.class == clazz;
    }

    private boolean isInternalLanguageInterface(Class<?> clazz) {
        return clazz.getName().endsWith(".cglib.proxy.Factory");
    }

}
