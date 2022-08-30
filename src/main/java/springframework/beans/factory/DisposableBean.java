package springframework.beans.factory;

import springframework.beans.BeansException;

public interface DisposableBean {

    void destroy() throws BeansException;

}
