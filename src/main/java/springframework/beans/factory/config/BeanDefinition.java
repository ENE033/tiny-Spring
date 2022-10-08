package springframework.beans.factory.config;

import springframework.beans.PropertyValues;
import springframework.core.io.Resource;

import java.util.Objects;


/**
 * 源码中：
 * BeanDefinition是一个接口，接口内定义了大部分beanDefinition属性的getter和setter
 * BeanDefinition由AbstractBeanDefinition抽象类实现，定义出beanDefinition的属性
 * <p>
 * XML 定义 Bean >>>>> GenericBeanDefinition
 * 通过 @Component 以及派生注解定义 Bean >>>>> ScannedGenericBeanDefinition
 * 通过 @Import 导入 Bean >>>>> AnnotatedGenericBeanDefinition
 * 通过 @Bean 定义的方法 >>>>> ConfigurationClassBeanDefinition
 * 在 Spring BeanFactory 初始化 Bean 的前阶段，会根据 BeanDefinition 生成一个合并后的 RootBeanDefinition 对象
 * <p>
 * 在本项目中仅实现了@Component和xml方式
 */

/**
 * 本类将BeanDefinition和AbstractBeanDefinition结合在一起，即定义属性又定义出各种操作属性的方法
 */
public class BeanDefinition {

    //单例模式的标识
    public static String SCOPE_SINGLETON = "singleton";

    //原型模式的标识
    public static String SCOPE_PROTOTYPE = "prototype";

    //bean的类型
    private volatile Object beanClass;

    //bean的属性集
    private PropertyValues propertyValues;

    //bean的来源
    private Resource resource;

    //bean的初始化方法名
    private String initMethodName;

    //bean的销毁方法名
    private String destroyMethodName;

    //bean的作用域，默认为单例
    private String scope = SCOPE_SINGLETON;

    private boolean singleton = true;

    private boolean prototype = false;


    public BeanDefinition(Class<?> beanClass) {
        this(beanClass, new PropertyValues());
    }

    public BeanDefinition(Class<?> beanClass, PropertyValues propertyValues) {
        this.beanClass = beanClass;
        this.propertyValues = propertyValues;
    }

    public void setScope(String scope) {
        this.scope = scope;
        this.singleton = SCOPE_SINGLETON.equals(scope);
        this.prototype = SCOPE_PROTOTYPE.equals(scope);
    }

    public String getScope() {
        return scope;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public boolean isPrototype() {
        return prototype;
    }

    public PropertyValues getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }

    public Class<?> getBeanClass() {
        return (Class<?>) beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public String getInitMethodName() {
        return initMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanDefinition that = (BeanDefinition) o;
        return Objects.equals(beanClass, that.beanClass) && Objects.equals(propertyValues, that.propertyValues) && Objects.equals(resource, that.resource) && Objects.equals(initMethodName, that.initMethodName) && Objects.equals(destroyMethodName, that.destroyMethodName) && Objects.equals(scope, that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanClass, propertyValues, resource, initMethodName, destroyMethodName, scope);
    }
}
