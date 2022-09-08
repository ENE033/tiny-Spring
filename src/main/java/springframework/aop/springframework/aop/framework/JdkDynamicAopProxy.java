package springframework.aop.springframework.aop.framework;

import springframework.aop.springframework.aop.AdvisedSupport;
import springframework.aop.aopalliance.intercept.MethodInterceptor;
import springframework.beans.ClassUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk原生动态代理
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    //AOP 代理配置管理器
    private final AdvisedSupport advised;

    public JdkDynamicAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    //执行方法拦截器或调用原方法
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //判断方法是否与目标类匹配
        if (advised.getMethodMatcher().matches(method, advised.getTargetSource().getTargetClass())) {
            //获取方法拦截器
            MethodInterceptor methodInterceptor = advised.getMethodInterceptor();
            //执行方法拦截器中的增加逻辑代码，传入一个ReflectiveMethodInvocation用于执行原方法
            return methodInterceptor.invoke(new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(), method, args));
        }
        //如果不是切点，则反射执行原方法
        return method.invoke(advised.getTargetSource().getTarget(), args);
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(
                ClassUtil.getDefaultClassLoader(),
                advised.getTargetSource().getTargetClass().getInterfaces(),
                this);
    }


}
