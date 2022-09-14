package springframework.beans.factory.support;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.NoOp;
import springframework.beans.BeansException;
import springframework.beans.factory.BeanFactory;
import springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;

public class CglibSubclassingInstantiationStrategy extends SimpleInstantiationStrategy {

    @Override
    protected Object instantiateWithMethodInjection(BeanDefinition bd, String beanName, BeanFactory owner) {
        return instantiateWithMethodInjection(bd, beanName, owner, null);
    }

    @Override
    protected Object instantiateWithMethodInjection(BeanDefinition bd, String beanName, BeanFactory owner,
                                                    Constructor<?> ctor, Object... args) {

        // Must generate CGLIB subclass...
        return new CglibSubclassCreator(bd, owner).instantiate(ctor, args);
    }

    private static class CglibSubclassCreator {

        private final BeanDefinition beanDefinition;

        private final BeanFactory beanFactory;

        public CglibSubclassCreator(BeanDefinition beanDefinition, BeanFactory beanFactory) {
            this.beanDefinition = beanDefinition;
            this.beanFactory = beanFactory;
        }

        public Object instantiate(Constructor<?> ctor, Object... args) throws BeansException {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(beanDefinition.getBeanClass());
            enhancer.setCallback(new NoOp() {
                @Override
                public int hashCode() {
                    return super.hashCode();
                }
            });
            if (ctor == null || args == null) {
                return enhancer.create();
            } else {
                return enhancer.create(ctor.getParameterTypes(), args);
            }
        }

        public Object instantiate() throws BeansException {
            return instantiate(null, (Object) null);
        }
    }


}
