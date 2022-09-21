package entity.aop;

import springframework.aop.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

public class UserServiceAfterReturnAdvice implements AfterReturningAdvice {
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) {
        System.out.println(target + "的方法" + method.getName() + "执行完毕，返回值是：" + returnValue);
    }
}
