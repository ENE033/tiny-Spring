import entity.PetService;
import entity.UserDao;
import entity.aop.IUserService;
import entity.aop.UserService;
import entity.aop.UserService2;
import entity.spring.KnownAll;
import entity.spring.event.CustomEvent;
import org.junit.Test;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import springframework.util.ClassUtils;

import java.lang.reflect.Proxy;

public class springTest {

    @Test
    public void test0() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("xml.xml");
        context.registerShutdownHook();
        UserDao userDao = context.getBean("userDao", UserDao.class);
        System.exit(-1);
    }

    @Test
    public void test1() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        KnownAll knownAll = context.getBean("knownAll", KnownAll.class);
        System.out.println(knownAll.getClassLoader());
        System.out.println(knownAll.getApplicationContext());
        System.out.println(knownAll.getBeanName());
        System.out.println(knownAll.getBeanFactory());

        knownAll.setBeanName("sb");
        System.out.println(knownAll.getBeanName());

    }

    @Test
    public void test2() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        PetService petService = context.getBean("petService", PetService.class);
        petService.getiPetDao().queryPetName("eqw");
//        context.getBean("user");
//        context.getBean("user");
    }

    @Test
    public void test3() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        context.publishEvent(new CustomEvent(context, 121, "wrqt"));
        context.registerShutdownHook();
//        Arrays.stream(context.getBeanFactory().getSingletonNames()).forEach(System.out::println);
    }

    @Test
    public void test4() {

        IUserService userService = new UserService();
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setInterfaces(IUserService.class.getInterfaces());
        advisedSupport.setTargetSource(new TargetSource() {
            @Override
            public Class<?> getTargetClass() {
                return UserService.class;
            }

            @Override
            public boolean isStatic() {
                return false;
            }

            @Override
            public Object getTarget() throws Exception {
                return userService;
            }

            @Override
            public void releaseTarget(Object target) throws Exception {
            }
        });

        DefaultAopProxyFactory defaultAopProxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = defaultAopProxyFactory.createAopProxy(advisedSupport);

        IUserService proxyInstance = (IUserService) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), userService.getClass().getInterfaces(), (proxy, method, args) -> {
//            if (method.getReturnType() != String.class) {
//                return method.invoke(userService, args);
//            }
            long start = System.currentTimeMillis();
            try {
                Object proceed = method.invoke(userService, args);
                System.out.println(proceed);
                return proceed;
            } finally {
                System.out.println("监控 - Begin By AOP");
                System.out.println("方法名称：" + method.getName());
                System.out.println("方法耗时：" + (System.currentTimeMillis() - start) + "ms");
                System.out.println("监控 - End\r\n");
            }
        });

        proxyInstance.register("1234");
        proxyInstance.addInfo();
        IUserService proxy = (IUserService) aopProxy.getProxy();
        System.out.println(proxy.queryUserInfo());
    }

    @Test
    public void test5() {
        IUserService userService = new UserService2();
        IUserService o = (IUserService) Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), userService.getClass().getInterfaces(), (proxy, method, args) -> {
            long start = System.currentTimeMillis();
            try {
                Object proceed = method.invoke(userService, args);
                System.out.println(proceed);
                return proceed;
            } finally {
                System.out.println("监控 - Begin By AOP");
                System.out.println("方法名称：" + method.getName());
                System.out.println("方法耗时：" + (System.currentTimeMillis() - start) + "ms");
                System.out.println("监控 - End\r\n");
            }
        });
        o.queryUserInfo();
        o.addInfo();
        o.register("3124");
    }


}
