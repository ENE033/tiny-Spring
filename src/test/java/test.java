import entity.User;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.support.DefaultListableBeanFactory;
import springframework.beans.factory.support.SimpleInstantiationStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class test {
    @Test
    public void test0() {
        ApplicationContext context = new ClassPathXmlApplicationContext("xml.xml");
        User user = (User) context.getBean("user","dwq",12);
        System.out.println(user);
        User user1 = (User) context.getBean("user","dwrwq",142);
        System.out.println(user1);
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

    @Test
    public void test2() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.setInstantiationStrategy(new SimpleInstantiationStrategy());
        BeanDefinition beanDefinition = new BeanDefinition(User.class);
        beanFactory.registerBeanDefinition("user", beanDefinition);
        System.out.println(int.class);
        System.out.println(Integer.class.getSuperclass().getSuperclass().getSuperclass());
        System.out.println(List.class);
        System.out.println(Arrays.toString(ArrayList.class.getInterfaces()));
        System.out.println(beanFactory.getBean("user", User.class, "xiaoming", 12));
//        System.out.println(beanFactory.getBean("user", User.class, "rew", 484));

    }

}
