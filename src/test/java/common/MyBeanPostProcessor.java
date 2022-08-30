package common;

import entity.User;
import springframework.beans.BeansException;
import springframework.beans.factory.config.BeanPostProcessor;

public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//        if ("user".equals(beanName)) {
//            User user = (User) bean;
//            user.read();
//            user.setAge(30);
//        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
