package springframework.beans.factory.support;

import springframework.beans.factory.DisposableBean;
import springframework.beans.BeansException;
import springframework.beans.factory.config.SingletonBeanRegistry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    //一级缓存
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    //实现了DisposableBean的bean缓存
    private final Map<String, Object> disposableBeans = new LinkedHashMap<>();

    public void registerDisposableBean(String beanName, DisposableBean bean) {
        this.disposableBeans.put(beanName, bean);
    }

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
        return singletonObjects.get(beanName);
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
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
