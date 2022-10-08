package springframework.aop.aopalliance.intercept;

/**
 * 这个接口表示程序中的调用。调用是一个连接点，可以被拦截器拦截。
 */
public interface Invocation extends Joinpoint {
    // 以数组对象的形式获取参数。可以通过改变数组中元素的值来改变参数
    Object[] getArguments();
}
