package springframework.beans.factory.annotation;

import springframework.beans.PropertyValues;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Collection;

public class InjectionMetadata {

    private final Class<?> targetClass;

    private final Collection<InjectedElement> injectedElements;

    public InjectionMetadata(Class<?> targetClass, Collection<InjectedElement> injectedElements) {
        this.targetClass = targetClass;
        this.injectedElements = injectedElements;
    }

    public void inject(Object target, String beanName, PropertyValues pvs) {
        for (InjectedElement injectedElement : injectedElements) {
            injectedElement.inject(target, beanName, pvs);
        }
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Collection<InjectedElement> getInjectedElements() {
        return injectedElements;
    }

    public static abstract class InjectedElement {

        protected final Member member;

        protected final boolean isField;

        public InjectedElement(Member member) {
            this.member = member;
            this.isField = (member instanceof Field);
        }

        public Member getMember() {
            return member;
        }

        public boolean isField() {
            return isField;
        }

        public abstract void inject(Object target, String beanName, PropertyValues pvs);

        public abstract Object getResourceToInject(Object target, String beanName);

//        public boolean checkPropertySkipping(PropertyValues pvs, String name) {
//            if (pvs == null) {
//                return false;
//            } else {
//                pvs.getPropertyValue(name);
//            }
//        }
    }

}
