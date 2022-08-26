import entity.User;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.support.DefaultListableBeanFactory;

public class test {
    @Test
    public void test0() {
        ApplicationContext context = new ClassPathXmlApplicationContext();
    }

    @Test
    public void test1() {
        DefaultListableBeanFactory listableBeanFactory = new DefaultListableBeanFactory();
        BeanDefinition beanDefinition = new BeanDefinition(User.class);
        listableBeanFactory.registerBeanDefinition("user", beanDefinition);
        Object user = listableBeanFactory.getBean("user");
        User user1 = listableBeanFactory.getBean("user", User.class);
        System.out.println(user);
        System.out.println(user1);
    }

}
