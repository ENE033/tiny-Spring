import entity.User;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class springTest {

    @Test
    public void test0() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"xml.xml"}, false);
        context.refresh();
        User user = (User) context.getBean("user");
        System.out.println(user.getClass());
    }

}
