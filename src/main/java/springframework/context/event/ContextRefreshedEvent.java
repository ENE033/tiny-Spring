package springframework.context.event;

import springframework.context.ApplicationContext;

/**
 * 监听刷新的事件
 */
public class ContextRefreshedEvent extends ApplicationContextEvent {
    public ContextRefreshedEvent(ApplicationContext source) {
        super(source);
    }
}
