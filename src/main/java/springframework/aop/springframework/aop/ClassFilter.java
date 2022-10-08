package springframework.aop.springframework.aop;


/**
 * 类匹配器，用于切点找到目标接口和目标类
 * <p>
 * 限制切入点匹配或引入到给定目标类集的过滤器。
 * 可以作为切入点的一部分，也可以作为IntroductionAdvisor的整个目标。
 * 这个接口的具体实现通常应该提供Object.equals(Object)和Object. hashcode()的正确实现，
 * 以便允许过滤器在缓存场景中使用——例如，在CGLIB生成的代理中。
 */
public interface ClassFilter {
    boolean matches(Class<?> clazz);
}
