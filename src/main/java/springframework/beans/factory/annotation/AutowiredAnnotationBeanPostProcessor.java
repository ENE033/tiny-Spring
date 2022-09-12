package springframework.beans.factory.annotation;

import cn.hutool.core.bean.BeanUtil;
import springframework.beans.PropertyValues;
import springframework.beans.factory.annotation.InjectionMetadata;
import springframework.beans.factory.config.ConfigurableListableBeanFactory;
import springframework.beans.BeansException;
import springframework.beans.factory.BeanFactory;
import springframework.beans.factory.BeanFactoryAware;
import springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<>();

    private final String requiredParameterName = "required";

    private final boolean requiredParameterValue = true;

    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);

    @Override

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    public AutowiredAnnotationBeanPostProcessor() {
        autowiredAnnotationTypes.add(Value.class);
        autowiredAnnotationTypes.add(Autowired.class);
    }

    InjectionMetadata buildAutowiringMetadata(Class<?> clazz) {
        List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            for (Annotation annotation : declaredField.getDeclaredAnnotations()) {
                if (autowiredAnnotationTypes.contains(annotation.annotationType())) {
                    if (Modifier.isStatic(declaredField.getModifiers())) {
                        throw new BeansException(" Autowired annotation is not supported on static field ：" + declaredField.getName());
                    }
                    elements.add(new AutowiredFieldElement(declaredField));
                    break;
                }
            }
        }
        return new InjectionMetadata(clazz, elements);
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {

        InjectionMetadata metadata = buildAutowiringMetadata(beanFactory.getBeanDefinition(beanName).getBeanClass());

        metadata.inject(bean, beanName, pvs);

        return pvs;
    }


    private class AutowiredFieldElement extends InjectionMetadata.InjectedElement {

        public AutowiredFieldElement(Member member) {
            super(member);
        }

        @Override
        public void inject(Object target, String beanName, PropertyValues pvs) {
            Field member = (Field) this.member;
            Autowired autowired = member.getAnnotation(Autowired.class);
            if (autowired != null) {
                Class<?> type = member.getType();
                Map<String, ?> beansOfType = beanFactory.getBeansOfType(type);
                if (beansOfType.size() == 0) {
                    if (member.getAnnotation(Value.class) == null && autowired.required()) {
                        throw new BeansException(" There are no beans of this type ：" + type);
                    }
                } else if (beansOfType.size() > 1) {
                    Qualifier qualifier = member.getAnnotation(Qualifier.class);
                    if (qualifier == null) {
                        throw new BeansException(" There are multiple beans of the same type ：" + type);
                    } else {
                        Object qualifierBean = beansOfType.get(qualifier.value());
                        if (qualifierBean == null) {
                            throw new BeansException(" No qualifying bean of type " + type + " available ");
                        } else {
                            BeanUtil.setFieldValue(target, member.getName(), beansOfType.get(qualifier.value()));
                        }
                    }
                } else {
                    beansOfType.forEach((name, bean) -> {
                        BeanUtil.setFieldValue(target, member.getName(), bean);
                    });
                }
            }
            Value value = member.getAnnotation(Value.class);
            if (value != null) {
                BeanUtil.setFieldValue(target, member.getName(), value.value());
            }
        }

        @Override
        public Object getResourceToInject(Object target, String beanName) {
            return null;
        }
    }

    private class AutowiredMethodElement extends InjectionMetadata.InjectedElement {

        public AutowiredMethodElement(Member member) {
            super(member);
        }

        @Override
        public void inject(Object target, String beanName, PropertyValues pvs) {

        }

        @Override
        public Object getResourceToInject(Object target, String beanName) {
            return null;
        }
    }

}
