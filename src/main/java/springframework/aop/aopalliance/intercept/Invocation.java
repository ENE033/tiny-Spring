package springframework.aop.aopalliance.intercept;

public interface Invocation extends Joinpoint {
    Object[] getArguments();
}
