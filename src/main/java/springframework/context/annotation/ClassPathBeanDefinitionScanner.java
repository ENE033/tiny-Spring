package springframework.context.annotation;

import cn.hutool.core.util.StrUtil;
import springframework.beans.BeansException;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.support.BeanDefinitionRegistry;
import springframework.beans.factory.support.GenericBeanDefinition;
import springframework.stereotype.Component;

import java.util.Set;

public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

    private final BeanDefinitionRegistry registry;

    // 注入BeanDefinitionRegistry
    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public int doScan(String... basePackages) {
        int beanSize = 0;
        // 遍历类路径
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = findCandidateComponents(basePackage);
            for (BeanDefinition beanDefinition : candidateComponents) {
                // 设置bean的作用域
                beanDefinition.setScope(resolveBeanScope(beanDefinition));
                // 确定bean的id
                String beanName = determineBeanName(beanDefinition);
                if (registry.checkCandidate(beanName, beanDefinition)) {
                    registry.registerBeanDefinition(determineBeanName(beanDefinition), beanDefinition);
                    beanSize++;
                } else {
                    BeanDefinition existingBeanDefinition = registry.getBeanDefinition(beanName);
                    if (existingBeanDefinition instanceof ScannedGenericBeanDefinition) {
                        continue;
                    } else {
                        registry.mergeBeanDefinition(existingBeanDefinition, beanDefinition);
                        registry.registerBeanDefinition(beanName, existingBeanDefinition);
                    }
                }
            }
        }
        return beanSize;
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
