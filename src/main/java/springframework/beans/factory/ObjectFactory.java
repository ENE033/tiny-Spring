package springframework.beans.factory;

import springframework.beans.BeansException;

@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject() throws BeansException;
}
