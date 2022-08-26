package springframework.beans.factory;

import springframework.beans.BeansException;

public interface BeanFactory {

    Object getBean(String name) throws BeansException;

    <T> T getBean(String name, Class<T> requireType) throws BeansException;
}
