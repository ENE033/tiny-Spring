package springframework.beans.factory.config;


/**
 * 在源码中：
 * BeanReference是一个接口
 * 以抽象方式公开对 bean 名称的引用的接口。
 * 该接口不一定意味着对实际 bean 实例的引用；它只是表达了对 bean 名称的逻辑引用。
 * <p>
 * 实际使用的是RuntimeBeanReference实现类
 */

/**
 * 该类不一定意味着对实际 bean 实例的引用；它只是表达了对 bean 名称的逻辑引用。
 */
public class BeanReference {
    private final String beanName;

    public BeanReference(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}
