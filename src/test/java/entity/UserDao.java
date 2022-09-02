package entity;

import springframework.beans.BeansException;
import springframework.beans.factory.DisposableBean;
import springframework.beans.factory.InitializingBean;

public class UserDao implements InitializingBean, DisposableBean {

    public void initMethod() {
//        System.out.println("初始化方法");
    }


    public void destroyMethod() {
//        System.out.println("销毁方法");
    }


    @Override
    public void afterPropertiesSet() throws BeansException {
//        System.out.println("设置属性之后执行的方法");
    }

    @Override
    public void destroy() throws BeansException {
//        System.out.println("destroy方法");
    }
}
