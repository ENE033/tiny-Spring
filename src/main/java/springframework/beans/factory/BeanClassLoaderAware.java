package springframework.beans.factory;

import springframework.beans.BeansException;

/**
 * 实现该接口，能感知到所属的ClassLoader
 */
public interface BeanClassLoaderAware extends Aware {

    void setBeanClassLoader(ClassLoader classLoader) throws BeansException;
}
