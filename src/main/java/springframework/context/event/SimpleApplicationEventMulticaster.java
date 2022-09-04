package springframework.context.event;

import springframework.beans.factory.config.ConfigurableBeanFactory;
import springframework.context.ApplicationEvent;
import springframework.context.ApplicationListener;

import java.util.Collection;

public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {
    public SimpleApplicationEventMulticaster(ConfigurableBeanFactory beanFactory) {
        setBeanFactory(beanFactory);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void multicastEvent(ApplicationEvent event) {
        //获取对event感兴趣的监听器
        Collection<ApplicationListener> applicationListeners = getApplicationListeners(event);
        //监听器处理event
        for (ApplicationListener applicationListener : applicationListeners) {
            applicationListener.onApplicationEvent(event);
        }

    }
}
