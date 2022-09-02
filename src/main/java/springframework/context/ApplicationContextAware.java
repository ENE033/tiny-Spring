package springframework.context;

import springframework.beans.BeansException;
import springframework.beans.factory.Aware;


/**
 * 实现该接口，能感知到所属的ApplicationContext
 */
public interface ApplicationContextAware extends Aware {

    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;

}
