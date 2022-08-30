import entity.User;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

public class springTest {

    @Test
    public void test0() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("xml.xml");
        User user = (User) context.getBean("user");
        System.out.println(user);
    }

}
