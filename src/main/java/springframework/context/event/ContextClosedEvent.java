package springframework.context.event;

import springframework.context.ApplicationContext;

/**
 * 监听关闭的事件
 */
public class ContextClosedEvent extends ApplicationContextEvent {
    public ContextClosedEvent(ApplicationContext source) {
        super(source);
    }
}
