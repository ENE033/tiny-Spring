package springframework.context.support;

import springframework.beans.BeansException;
import springframework.beans.factory.ConfigurableListableBeanFactory;
import springframework.beans.factory.config.BeanFactoryPostProcessor;
import springframework.beans.factory.config.BeanPostProcessor;
import springframework.context.ConfigurableApplicationContext;
import springframework.core.io.DefaultResourceLoader;

import java.util.Map;

public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    @Override
    public void refresh() throws BeansException {

        //创建beanFactory并注册所有beanDefinition
        refreshBeanFactory();

        ConfigurableListableBeanFactory beanFactory = getBeanFactory();

        //激活所有beanFactoryPostProcessors
        invokeBeanFactoryPostProcessors(beanFactory);

        //注册所有BeanPostProcessors
        registerBeanPostProcessors(beanFactory);

        //预实例化所有bean
        beanFactory.preInstantiateSingletons();

    }

    @Override
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close, SHUTDOWN_HOOK_THREAD_NAME));
    }

    @Override
    public void close() {
        getBeanFactory().destroySingletons();
    }

    /**
     * 创建beanFactory，并加载所有beanDefinition
     *
     * @throws BeansException
     */
    protected abstract void refreshBeanFactory() throws BeansException;


    /**
     * 获取beanFactory
     *
     * @return
     */
    public abstract ConfigurableListableBeanFactory getBeanFactory();


    /**
     * 在实例化bean之前执行BeanFactoryPostProcessors
     *
     * @param beanFactory
     */
    private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
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
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorMap.values()) {
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
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
}
