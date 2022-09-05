package springframework.context.support;

import springframework.beans.BeansException;
import springframework.beans.factory.config.ConfigurableListableBeanFactory;
import springframework.beans.factory.support.DefaultListableBeanFactory;

public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {
    private DefaultListableBeanFactory beanFactory;

    @Override
    protected void refreshBeanFactory() throws BeansException {
        //创建beanFactory
        DefaultListableBeanFactory beanFactory = createBeanFactory();
        //注册所有beanDefinition
        loadBeanDefinitions(beanFactory);
        this.beanFactory = beanFactory;
    }

    private DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory();
    }


    @Override
    public ConfigurableListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    /**
     * 提供一个抽象方法，让其子类实现加载beanDefinitions的方式
     *
     * @param beanFactory
     */
    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory);

}
