package springframework.context.event;

import springframework.beans.BeansException;
import springframework.util.ClassUtils;
import springframework.beans.factory.BeanFactory;
import springframework.beans.factory.BeanFactoryAware;
import springframework.beans.factory.config.ConfigurableBeanFactory;
import springframework.context.ApplicationEvent;
import springframework.context.ApplicationListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanFactoryAware {
    public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

    private ConfigurableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new BeansException(" Not running in a ConfigurableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        this.applicationListeners.add(listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        this.applicationListeners.remove(listener);
    }

    protected Collection<ApplicationListener> getApplicationListeners(ApplicationEvent event) {
        LinkedList<ApplicationListener> allListeners = new LinkedList<>();
        //获取对传入事件感兴趣的监听器
        applicationListeners.forEach(listener -> {
            if (supportsEvent(listener, event)) {
                allListeners.add(listener);
            }
        });
        return allListeners;
    }


    protected boolean supportsEvent(ApplicationListener<?> applicationListener, ApplicationEvent event) {
        //获取监听器的类对象
        Class<? extends ApplicationListener> listenerClass = applicationListener.getClass();
        //判断该类是否cglib代理对象，获取目标类对象
        Class<?> targetClass = ClassUtils.isCglibProxyClass(listenerClass) ? listenerClass.getSuperclass() : listenerClass;
        //获取目标类对象的实现的接口
        Type genericInterface = targetClass.getGenericInterfaces()[0];
        //获取泛型参数的类型
        Type actualTypeArgument = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
        //获取泛型参数的类型名
        String className = actualTypeArgument.getTypeName();
        Class<?> eventClass;
        try {
            //通过类加载器获取泛型参数的类对象
            eventClass = ClassUtils.getDefaultClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new BeansException(" wrong event class name ：" + className, e);
        }
        //判断泛型参数的类是否被传入的event类实现或继承
        return eventClass.isAssignableFrom(event.getClass());
    }


}
