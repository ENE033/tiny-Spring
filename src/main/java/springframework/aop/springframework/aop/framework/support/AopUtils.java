package springframework.aop.springframework.aop.framework.support;

import springframework.aop.springframework.aop.Advised;
import springframework.aop.springframework.aop.AdvisedSupport;

import java.util.ArrayList;
import java.util.List;

public class AopUtils {

    /**
     * 确定要代理给定AOP配置的完整接口集。
     * 始终添加Advised的接口
     *
     * @param advisedSupport
     * @return
     */
    public static Class<?>[] completeProxiedInterfaces(AdvisedSupport advisedSupport) {
        // 获取AOP配置中的全部接口
        List<Class<?>> specifiedInterfaces = advisedSupport.getInterfaces();
        Class<?> targetClass = advisedSupport.getTargetSource().getTargetClass();
        // 如果没有接口，则判断目标类是不是接口
        if (specifiedInterfaces.size() == 0) {
            if (targetClass.isInterface()) {
                advisedSupport.setInterfaces(targetClass);
            }
            specifiedInterfaces = advisedSupport.getInterfaces();
        }
        List<Class<?>> proxiedInterfaces = new ArrayList<>(specifiedInterfaces);
        // 判断接口中是否存在Advised及其子接口，如果没有则要添加Advised接口
        if (!advisedSupport.isInterfaceProxied(Advised.class)) {
            proxiedInterfaces.add(Advised.class);
        }
        return proxiedInterfaces.toArray(new Class[0]);
    }

}
