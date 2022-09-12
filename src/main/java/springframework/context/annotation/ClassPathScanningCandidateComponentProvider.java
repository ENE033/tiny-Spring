package springframework.context.annotation;

import cn.hutool.core.util.ClassUtil;
import springframework.beans.factory.config.BeanDefinition;
import springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

public class ClassPathScanningCandidateComponentProvider {

    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackage, Component.class);
        for (Class<?> aClass : classes) {
            candidates.add(new ScannedGenericBeanDefinition(aClass));
        }
        return candidates;
    }
}
