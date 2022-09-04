package springframework.context.support;

import springframework.beans.BeansException;
import springframework.beans.factory.ConfigurableListableBeanFactory;
import springframework.beans.factory.config.BeanFactoryPostProcessor;
import springframework.beans.factory.config.BeanPostProcessor;
import springframework.context.ApplicationEvent;
import springframework.context.ApplicationEventPublisher;
import springframework.context.ApplicationListener;
import springframework.context.ConfigurableApplicationContext;
import springframework.context.event.ApplicationEventMulticaster;
import springframework.context.event.ContextClosedEvent;
import springframework.context.event.ContextRefreshedEvent;
import springframework.context.event.SimpleApplicationEventMulticaster;
import springframework.core.io.DefaultResourceLoader;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";

    private ApplicationEventMulticaster applicationEventMulticaster;


    @Override
    public void refresh() throws BeansException {

        //创建beanFactory并注册所有beanDefinition
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        //Note：this用于引用当前实例变量，无法在static上下文中引用this，this是非静态变量
        //配置工厂的标准上下文特征，例如上下文的 ClassLoader 和后处理器
        prepareBeanFactory(beanFactory);

        try {
            //实例化并激活所有beanFactoryPostProcessors
            invokeBeanFactoryPostProcessors(beanFactory);

            //实例化并注册所有beanPostProcessors
            registerBeanPostProcessors(beanFactory);

            //初始化事件发布者
            initApplicationEventMulticaster();

            //注册事件监听者
            registerListeners();

            //完成此上下文的 bean 工厂的初始化，初始化所有剩余的单例 bean
            finishBeanFactoryInitialization(beanFactory);

            //发布容器刷新完成事件
            finishRefresh();

        } catch (Exception e) {

            //执行bean的销毁方法
            close();

            throw new BeansException(" Exception encountered during context initialization ", e);
        }
    }

    //创建beanFactory并注册所有beanDefinition
    protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        refreshBeanFactory();
        return getBeanFactory();
    }

    //配置工厂的标准上下文特征，例如上下文的 ClassLoader 和后处理器
    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        //注册ApplicationContextAwareProcessor到beanFactory，
        //使所有实现了ApplicationContextAware的bean都能感知到当前上下文context
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
    }

    /**
     * 在实例化bean之前执行BeanFactoryPostProcessors
     *
     * @param beanFactory
     */
    private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        //getBeansOfType方法，实例化所有的BeanFactoryPostProcessor，并放入beanFactoryPostProcessorMap
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessorMap.values()) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    /**
     * 在Bean对象实例化之前执行注册BeanPostProcessor
     *
     * @param beanFactory
     */
    private void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        //getBeansOfType方法，实例化所有的BeanPostProcessor，并放入beanPostProcessorMap
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorMap.values()) {
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }


    private void initApplicationEventMulticaster() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
        beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, applicationEventMulticaster);
    }

    private void registerListeners() {
        Collection<ApplicationListener> applicationListeners = getBeansOfType(ApplicationListener.class).values();
        applicationListeners.forEach(listener -> applicationEventMulticaster.addApplicationListener(listener));
    }

    private void finishRefresh() {

        publishEvent(new ContextRefreshedEvent(this));
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        //事件多播器广播事件到相应的监听器中
        applicationEventMulticaster.multicastEvent(event);
    }

    /**
     * 完成此上下文的 bean 工厂的初始化，初始化所有剩余的单例 bean
     *
     * @param beanFactory
     */
    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        //预实例化所有bean
        beanFactory.preInstantiateSingletons();
    }


    /**
     * 获取指定类的bean，可用于获取beanFactoryPostProcessor和beanPostProcessor
     *
     * @param type
     * @param <T>
     * @return
     * @throws BeansException
     */
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getBeanFactory().getBeansOfType(type);
    }

    /**
     * 获取beanDefinitionMap中的所有bean的beanName
     *
     * @return
     */
    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    /**
     * 注册一个新的虚拟机关闭钩子
     */
    @Override
    public void registerShutdownHook() {
        //当虚拟机关闭的时候会执行close方法
        Runtime.getRuntime().addShutdownHook(new Thread(this::close, SHUTDOWN_HOOK_THREAD_NAME));
    }

    /**
     * 关闭工厂
     */
    @Override
    public void close() {
        //发布关闭事件
        publishEvent(new ContextClosedEvent(this));
        //执行bean的销毁方法
        getBeanFactory().destroySingletons();
    }

    /**
     * 获取beanFactory，交给子类实现
     *
     * @return
     */
    public abstract ConfigurableListableBeanFactory getBeanFactory();


    /**
     * 创建beanFactory，并加载所有beanDefinition
     *
     * @throws BeansException
     */
    protected abstract void refreshBeanFactory() throws BeansException;


}
