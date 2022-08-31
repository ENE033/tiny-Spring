import entity.User;
import entity.UserDao;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

public class springTest {

    @Test
    public void test0() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("xml.xml");
        context.registerShutdownHook();
        UserDao userDao = context.getBean("userDao", UserDao.class);
        System.exit(-1);
    }

}
