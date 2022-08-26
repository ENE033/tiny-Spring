package springframework.beans.factory;

import springframework.beans.BeansException;

public interface BeanFactory {
    //通过bean的id获取bean，无参构造器
    Object getBean(String name) throws BeansException;

    //通过bean的id获取bean，可以传入类型来识别，无参构造器
    <T> T getBean(String name, Class<T> requireType) throws BeansException;

    //通过bean的id获取bean，有参构造器
    Object getBean(String name, Object... args) throws BeansException;

}
