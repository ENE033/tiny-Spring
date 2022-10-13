package springframework.context.annotation;

import cn.hutool.core.util.StrUtil;
import springframework.beans.BeansException;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.support.BeanDefinitionRegistry;
import springframework.stereotype.Component;

import java.util.Set;

public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

    private final BeanDefinitionRegistry registry;

    // 注入BeanDefinitionRegistry
    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void doScan(String... basePackages) {
        // 遍历类路径
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = findCandidateComponents(basePackage);
            for (BeanDefinition beanDefinition : candidateComponents) {
                // 设置bean的作用域
                beanDefinition.setScope(resolveBeanScope(beanDefinition));
                // 确定bean的id
                String beanName = determineBeanName(beanDefinition);

                if (checkCandidate(beanName, beanDefinition)) {
                    registry.registerBeanDefinition(determineBeanName(beanDefinition), beanDefinition);
                }
            }
        }
    }

    /**
     * 检查beanName是否存在，如果存在是否兼容
     *
     * @param beanName
     * @param beanDefinition
     * @return true：beanName尚未注册，
     * false：beanName已经注册，但是beanName注册的beanDefinition与新的beanDefinition兼容
     * @throws BeansException 两个beanDefinition不兼容
     */
    public boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws BeansException {
        // 如果beanName对应的BeanDefinition不存在，返回true
        if (!registry.containsBeanDefinition(beanName)) {
            return true;
        }
        BeanDefinition existingDef = registry.getBeanDefinition(beanName);
        // 如果两个beanDefinition兼容，返回false，不兼容则抛异常
        if (isCompatible(beanDefinition, existingDef)) {
            return false;
        } else {
            throw new BeansException(" Duplicate beanName '" + beanName + "' is not allowed ");
        }
    }


    /**
     * 判断两个beanDefinition是否兼容
     *
     * @param newDefinition
     * @param existingDefinition
     * @return 如果existingDefinition不是一个ScannedGenericBeanDefinition，则不能兼容，返回false
     */
    public boolean isCompatible(BeanDefinition newDefinition, BeanDefinition existingDefinition) {
        if (!(existingDefinition instanceof ScannedGenericBeanDefinition)) {
            return true;
        }
        return false;
    }

    /**
     * 解析bean的作用域
     *
     * @param beanDefinition
     * @return
     */
    private String resolveBeanScope(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        // 获取beanClass中的Scope注解
        Scope scope = beanClass.getAnnotation(Scope.class);
        // 如果没有Scope注解
        // 则默认单例
        if (scope == null) {
            return BeanDefinition.SCOPE_SINGLETON;
        }
        // 获取Scope注解的值
        String beanScope = scope.value();
        if (beanScope.isEmpty() || beanScope.equals(BeanDefinition.SCOPE_SINGLETON)) {
            return BeanDefinition.SCOPE_SINGLETON;
        } else if (beanScope.equals(BeanDefinition.SCOPE_PROTOTYPE)) {
            return BeanDefinition.SCOPE_PROTOTYPE;
        } else {
            throw new BeansException(" Scope resolution of bean failed ：" + beanScope);
        }
    }

    /**
     * 确定bean的id
     *
     * @param beanDefinition
     * @return
     */
    private String determineBeanName(BeanDefinition beanDefinition) {
        // 获取目标Class
        Class<?> beanClass = beanDefinition.getBeanClass();
        // 获取Class上的Component注解
        Component component = beanClass.getAnnotation(Component.class);
        // 获取Component的值作为bean的id
        String beanName = component.value();
        // 如果没有，则使用beanClass的开头字母小写的字符串作为bean的id
        if (beanName.isEmpty()) {
            beanName = beanClass.getSimpleName();
            char[] chars = beanName.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            beanName = new String(chars);
            return beanName;
        }
        // 如果值是一串空字符串，也是不允许的
        if (beanName.trim().isEmpty()) {
            throw new BeansException(" The id of bean is illegal ：" + beanName);
        }
        return beanName;
    }

}
