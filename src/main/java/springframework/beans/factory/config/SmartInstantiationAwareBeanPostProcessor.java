package springframework.beans.factory.config;


public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {
    Object getEarlyBeanReference(Object bean, String beanName);
}
