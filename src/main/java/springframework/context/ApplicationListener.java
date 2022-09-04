package springframework.context;

import java.util.EventListener;

/**
 * 由应用程序事件监听器实现的接口
 *
 * @param <E>
 */
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
    /**
     * 处理应用上下文的事件
     *
     * @param event
     */
    void onApplicationEvent(E event);
}
