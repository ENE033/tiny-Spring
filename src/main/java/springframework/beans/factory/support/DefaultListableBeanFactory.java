package springframework.beans.factory.support;

import springframework.beans.BeansException;
import springframework.beans.PropertyValue;
import springframework.beans.PropertyValues;
import springframework.beans.factory.config.ConfigurableListableBeanFactory;
import springframework.beans.factory.config.BeanDefinition;
import springframework.context.annotation.ScannedGenericBeanDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new BeansException(" No bean named '" + beanName + "' is defined ");
        }
        return beanDefinition;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    /**
     * 实例化并获取指定类型的bean
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        //map集合，键为String，值为T
        Map<String, T> result = new HashMap<>();
        try {
            beanDefinitionMap.forEach((beanName, beanDefinition) -> {
                //获取bean的Class
                Class<?> beanClass = beanDefinition.getBeanClass();
                //判断beanClass是否继承了type类或实现了type接口
                if (type.isAssignableFrom(beanClass)) {
                    //获取bean对象
                    result.put(beanName, (T) getBean(beanName));
                }
            });
        } catch (BeansException e) {
            throw new BeansException(" Failed to get the specified class ：" + type.getName(), e);
        }
        return result;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }


    @Override
    //初始化所有单例bean
    public void preInstantiateSingletons() throws BeansException {
        for (String beanDefinitionName : getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = getBeanDefinition(beanDefinitionName);
            //判断是否单例bean
            if (beanDefinition.isSingleton()) {
                getBean(beanDefinitionName);
            }
        }
    }

    @Override
    public boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws BeansException {
        if (!containsBeanDefinition(beanName)) {
            return true;
        }
        BeanDefinition existingDef = getBeanDefinition(beanName);
        if (isCompatible(beanDefinition, existingDef)) {
            return false;
        } else {
            throw new BeansException(" Duplicate beanName '" + beanName + "' is not allowed ");
        }
    }

    @Override
    public boolean isCompatible(BeanDefinition newDefinition, BeanDefinition existingDefinition) {
        if (newDefinition.getBeanClass() == existingDefinition.getBeanClass()) {
            return true;
        }
        return false;
    }

    /**
     * 将已存在的beanDefinition合并到新的beanDefinition中
     *
     * @param newDefinition
     * @param existingDefinition
     */
    public void mergeBeanDefinition(BeanDefinition newDefinition, BeanDefinition existingDefinition) {
        if (newDefinition.getResource() == null) {
            newDefinition.setResource(existingDefinition.getResource());
        }
        if (newDefinition.getInitMethodName() == null) {
            newDefinition.setInitMethodName(existingDefinition.getInitMethodName());
        }
        if (newDefinition.getDestroyMethodName() == null) {
            newDefinition.setDestroyMethodName(existingDefinition.getDestroyMethodName());
        }
        PropertyValues newPropertyValues = newDefinition.getPropertyValues();
        for (PropertyValue propertyValue : existingDefinition.getPropertyValues().getPropertyValues()) {
            if (newPropertyValues.getPropertyValue(propertyValue.getName()) == null) {
                newPropertyValues.addPropertyValue(propertyValue);
            }
        }
    }

}
