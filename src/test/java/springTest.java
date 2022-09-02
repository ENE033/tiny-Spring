import entity.PetService;
import entity.User;
import entity.UserDao;
import entity.spring.KnownAll;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

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


}
