package springframework.beans.factory.support;

import springframework.beans.factory.DisposableBean;
import springframework.beans.BeansException;
import springframework.beans.factory.ObjectFactory;
import springframework.beans.factory.config.SingletonBeanRegistry;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {


    //一级缓存
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(64);

    //二级缓存
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(8);

    //三级缓存
    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>(8);

    //正在被创建的beanName集合
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));


    //实现了DisposableBean的bean缓存
    private final Map<String, Object> disposableBeans = new LinkedHashMap<>();

    /**
     * 获取所有的disposableBean并执行destroy方法
     */
    public void destroySingletons() {
        String[] disposableBeanNames = this.disposableBeans.keySet().toArray(new String[0]);
        for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
            String disposableBeanName = disposableBeanNames[i];
            DisposableBean disposableBean = (DisposableBean) disposableBeans.remove(disposableBeanName);
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                throw new BeansException(" Destroy method on bean with name '" + disposableBeanName + "' threw an exception ", e);
            }
        }
    }

    @Override
    public Object getSingleton(String beanName) {
        return getSingleton(beanName, true);
    }

    public Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject == null && singletonsCurrentlyInCreation.contains(beanName)) {
            singletonObject = earlySingletonObjects.get(beanName);
            if (singletonObject == null && allowEarlyReference) {
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    //执行三级缓存中的匿名内部类的getObject方法，获取对象或对象的代理对象
                    singletonObject = singletonFactory.getObject();
                    earlySingletonObjects.put(beanName, singletonObject);
                    singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }

    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject == null) {
            beforeSingletonCreation(beanName);
            try {
                singletonObject = singletonFactory.getObject();
                addSingleton(beanName, singletonObject);
            } catch (BeansException e) {
                throw new BeansException(" Bean create failed ：" + beanName, e);
            } finally {
                afterSingletonCreation(beanName);
            }
        }
        return singletonObject;
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        Object oldObject = singletonObjects.get(beanName);
        if (singletonObjects.get(beanName) != null) {
            throw new BeansException(" Could not register object [" + singletonObject + "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound ");
        }
        addSingleton(beanName, singletonObject);
    }

    public void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
        singletonFactories.remove(beanName);
        earlySingletonObjects.remove(beanName);
    }


    public void destroySingleton(String beanName) {
        removeSingleton(beanName);
        DisposableBean disposableBean = (DisposableBean) disposableBeans.get(beanName);
        if (disposableBean != null) {
            disposableBean.destroy();
        }
    }

    public void removeSingleton(String beanName) {
        singletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
        earlySingletonObjects.remove(beanName);
    }

    public void registerDisposableBean(String beanName, DisposableBean bean) {
        this.disposableBeans.put(beanName, bean);
    }

    public void addSingletonFactory(String beanName, ObjectFactory<?> objectFactory) {
        if (!singletonObjects.containsKey(beanName)) {
            singletonFactories.put(beanName, objectFactory);
            earlySingletonObjects.remove(beanName);
        }
    }

    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    protected void beforeSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new BeansException(" Requested bean is currently in creation: Is there an unresolvable circular reference? ：" + beanName);
        }
    }

    protected void afterSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new BeansException("Singleton '" + beanName + "' isn't currently in creation");
        }
    }


    /**
     * todo
     * 记得删
     *
     * @return
     */
    public Map<String, Object> getSingletonObjects() {
        return singletonObjects;
    }
}
