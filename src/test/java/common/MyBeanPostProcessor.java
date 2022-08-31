package common;

import entity.User;
import entity.UserDao;
import springframework.beans.BeansException;
import springframework.beans.factory.config.BeanPostProcessor;

public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if ("userDao".equals(beanName)) {
            UserDao userDao = (UserDao) bean;
            System.out.println("初始化前");
            System.out.println(userDao.getClass());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if ("userDao".equals(beanName)) {
            UserDao userDao = (UserDao) bean;
            System.out.println("初始化后");
            System.out.println(userDao.toString());
        }
        return bean;
    }
}
