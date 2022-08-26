package springframework.beans.factory.config;

import org.springframework.lang.Nullable;

public class BeanDefinition {

    private volatile Object beanClass;

    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() {
        return (Class<?>) beanClass;
    }

    public void setBeanClass(@Nullable Class<?> beanClass) {
        this.beanClass = beanClass;
    }
}
