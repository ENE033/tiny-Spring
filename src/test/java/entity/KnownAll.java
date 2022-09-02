package entity;

import springframework.beans.BeansException;
import springframework.beans.factory.BeanClassLoaderAware;
import springframework.beans.factory.BeanFactory;
import springframework.beans.factory.BeanFactoryAware;
import springframework.beans.factory.BeanNameAware;
import springframework.context.ApplicationContext;
import springframework.context.ApplicationContextAware;

public class KnownAll implements ApplicationContextAware, BeanNameAware, BeanClassLoaderAware, BeanFactoryAware {

    ClassLoader classLoader;
    BeanFactory beanFactory;
    String beanName;
    ApplicationContext applicationContext;

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public String getBeanName() {
        return beanName;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) throws BeansException {
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
