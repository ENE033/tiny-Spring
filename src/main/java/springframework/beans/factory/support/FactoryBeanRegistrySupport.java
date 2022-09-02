package springframework.beans.factory.support;

import springframework.beans.BeansException;
import springframework.beans.factory.FactoryBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {

    //缓存被factoryBeans创建的单例bean
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    /**
     * 从factoryBeanObjectCache缓存中获取已被缓存的单例bean
     *
     * @param beanName
     * @return
     */
    protected Object getCachedObjectForFactoryBean(String beanName) {
        return this.factoryBeanObjectCache.get(beanName);
    }


    /**
     * 获取factoryBean生产的bean，其中有单例bean和原型bean
     *
     * @param factory
     * @param beanName
     * @return
     */
    protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName) {
        if (factory.isSingleton()) {
            Object object = this.factoryBeanObjectCache.get(beanName);
            if (object == null) {
                object = doGetObjectFromFactoryBean(factory, beanName);
                this.factoryBeanObjectCache.put(beanName, object);
            }
            return object;
        } else {
            return doGetObjectFromFactoryBean(factory, beanName);
        }
    }

    /**
     * factoryBean生产bean
     *
     * @param factory
     * @param beanName
     * @return
     */
    protected Object doGetObjectFromFactoryBean(FactoryBean<?> factory, String beanName) {
        try {
            return factory.getObject();
        } catch (Exception e) {
            throw new BeansException(" FactoryBean threw exception on object[" + beanName + "] creation ", e);
        }
    }

}
