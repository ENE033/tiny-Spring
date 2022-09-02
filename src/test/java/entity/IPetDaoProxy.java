package entity;

import springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class IPetDaoProxy implements FactoryBean<IPetDao>, org.springframework.beans.factory.FactoryBean<IPetDao> {
    @Override
    public IPetDao getObject() throws Exception {
        InvocationHandler invocationHandler = (proxy, method, args) -> {
            System.out.println("方法开始前");
            System.out.println(args[0]);
            System.out.println("方法结束");
            return null;
        };
        return (IPetDao) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{IPetDao.class}, invocationHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return IPetDao.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
