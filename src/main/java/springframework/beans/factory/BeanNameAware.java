package springframework.beans.factory;

/**
 * 实现该接口，能感知到所属的BeanName
 */
public interface BeanNameAware extends Aware {
    void setBeanName(String name);
}
