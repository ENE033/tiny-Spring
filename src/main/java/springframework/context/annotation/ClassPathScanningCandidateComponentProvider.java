package springframework.context.annotation;

import cn.hutool.core.util.ClassUtil;
import springframework.beans.factory.config.BeanDefinition;
import springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 一个Component提供者，从基本包中提供候选Component。
 * 如果可以扫描classpath，则可以使用索引。
 * 通过应用排除和包含过滤器来识别候选组件。
 * 支持注解/超类上带有Indexed注解的包含过滤器:
 * 如果指定了任何其他包含过滤器，索引将被忽略，并使用classpath扫描。
 */
public class ClassPathScanningCandidateComponentProvider {

    /**
     * 扫描类路径以查找候选Component。
     *
     * @param basePackage
     * @return
     */
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackage, Component.class);
        for (Class<?> aClass : classes) {
            candidates.add(new ScannedGenericBeanDefinition(aClass));
        }
        return candidates;
    }
}
