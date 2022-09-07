package entity.aop;

import springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class UserServiceBeforeAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Object target, Method method, Object[] args) {
        System.out.println("拦截方法" + method.getName());
    }
}
