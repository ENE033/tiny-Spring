package common;

import springframework.beans.BeansException;
import springframework.beans.factory.config.ConfigurableListableBeanFactory;
import springframework.beans.factory.config.BeanFactoryPostProcessor;

public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        System.out.println("hello beanFactoryProcessor");
//        beanFactory.getBeanDefinition("user").getPropertyValues().addPropertyValue(new PropertyValue("age", 100));
//        beanFactory.getBeanDefinition("user").getPropertyValues().addPropertyValue(new PropertyValue("name", "xxx"));
    }
}
