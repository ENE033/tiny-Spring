package springframework.aop.springframework.aop.framework.support;

import springframework.aop.springframework.aop.Advised;
import springframework.aop.springframework.aop.AdvisedSupport;

import java.util.ArrayList;
import java.util.List;

public class AopUtils {

    public static Class<?>[] completeProxiedInterfaces(AdvisedSupport advisedSupport) {
        List<Class<?>> specifiedInterfaces = advisedSupport.getInterfaces();
        Class<?> targetClass = advisedSupport.getTargetSource().getTargetClass();
        if (specifiedInterfaces.size() == 0) {
            if (targetClass.isInterface()) {
                advisedSupport.setInterfaces(targetClass);
            }
            specifiedInterfaces = advisedSupport.getInterfaces();
        }
        List<Class<?>> proxiedInterfaces = new ArrayList<>(specifiedInterfaces);
        if (!advisedSupport.isInterfaceProxied(Advised.class)) {
            proxiedInterfaces.add(Advised.class);
        }
        return proxiedInterfaces.toArray(new Class[0]);
    }

}
