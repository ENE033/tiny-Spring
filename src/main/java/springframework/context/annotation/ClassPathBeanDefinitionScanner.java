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

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public int doScan(String... basePackages) {
        int beanSize = 0;
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = findCandidateComponents(basePackage);
            for (BeanDefinition beanDefinition : candidateComponents) {
                beanDefinition.setScope(resolveBeanScope(beanDefinition));
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

    private String resolveBeanScope(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Scope scope = beanClass.getAnnotation(Scope.class);
        if (scope == null) {
            return BeanDefinition.SCOPE_SINGLETON;
        }
        String beanScope = scope.value();
        if (beanScope.isEmpty() || beanScope.equals(BeanDefinition.SCOPE_SINGLETON)) {
            return BeanDefinition.SCOPE_SINGLETON;
        } else if (beanScope.equals(BeanDefinition.SCOPE_PROTOTYPE)) {
            return BeanDefinition.SCOPE_PROTOTYPE;
        } else {
            throw new BeansException(" Scope resolution of bean failed ：" + beanScope);
        }
    }

    private String determineBeanName(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Component component = beanClass.getAnnotation(Component.class);
        String beanName = component.value();
        if (beanName.isEmpty()) {
            beanName = beanClass.getSimpleName();
            char[] chars = beanName.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            beanName = new String(chars);
            return beanName;
        }
        if (beanName.trim().isEmpty()) {
            throw new BeansException(" The id of bean is illegal ：" + beanName);
        }
        return beanName;
    }

}
