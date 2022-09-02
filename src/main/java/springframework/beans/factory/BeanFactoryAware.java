package springframework.beans.factory;

import springframework.beans.BeansException;

/**
 * 容器感知类
 * 实现该接口，能感知到所属的BeanFactory
 */
public interface BeanFactoryAware extends Aware {
    void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}
