package springframework.aop.springframework.aop;

/**
 * 被代理对象信息管理，用于获取被代理对象和被代理对象的类
 */
public class TargetSource {

    private final Object target;

    public TargetSource(Object target) {
        this.target = target;
    }

    public Object getTarget() {
        return target;
    }

    public Class<?> getTargetClass() {
        return target.getClass();
    }
}
