package springframework.context.event;

import springframework.context.ApplicationContext;
import springframework.context.ApplicationEvent;

/**
 * 定义事件的抽象类，所有事件都要继承这个类
 */
public class ApplicationContextEvent extends ApplicationEvent {
    public ApplicationContextEvent(ApplicationContext source) {
        super(source);
    }

    public final ApplicationContext getApplicationContext() {
        return (ApplicationContext) getSource();
    }
}
