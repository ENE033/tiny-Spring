package springframework.beans.factory;

import springframework.beans.BeansException;

public interface InitializingBean {

    /**
     * bean填充了属性之后调用
     *
     * @throws Exception
     */
    void afterPropertiesSet() throws BeansException;

}
