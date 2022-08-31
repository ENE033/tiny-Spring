package springframework.context;

import springframework.beans.BeansException;

public interface ConfigurableApplicationContext extends ApplicationContext {

    String SHUTDOWN_HOOK_THREAD_NAME = "SpringContextShutdownHook";

    /**
     * 刷新容器
     *
     * @throws BeansException
     */
    void refresh() throws BeansException;

    void registerShutdownHook();

    void close();
}
