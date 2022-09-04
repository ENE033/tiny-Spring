package springframework.context.event;

import springframework.context.ApplicationEvent;
import springframework.context.ApplicationListener;

public interface ApplicationEventMulticaster {

    /**
     * 增加一个监听器
     *
     * @param listener
     */
    void addApplicationListener(ApplicationListener<?> listener);

    /**
     * 删除一个监听器
     *
     * @param listener
     */
    void removeApplicationListener(ApplicationListener<?> listener);

    /**
     * 广播事件到适当的监听器中
     *
     * @param event
     */
    void multicastEvent(ApplicationEvent event);

}
