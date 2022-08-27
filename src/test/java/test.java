import entity.Pet;
import entity.User;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import springframework.beans.factory.PropertyValue;
import springframework.beans.factory.PropertyValues;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.config.BeanReference;
import springframework.beans.factory.support.CglibSubclassingInstantiationStrategy;
import springframework.beans.factory.support.DefaultListableBeanFactory;
import springframework.beans.factory.support.SimpleInstantiationStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class test {
    @Test
    public void test0() {
        ApplicationContext context = new ClassPathXmlApplicationContext("xml.xml");
        User user = (User) context.getBean("user", "dwq", 12);
        System.out.println(user);
        User user1 = (User) context.getBean("user", "dwrwq", 142);
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
//        beanFactory.setSimpleStrategy(true);
        BeanDefinition beanDefinition = new BeanDefinition(User.class);

        beanFactory.registerBeanDefinition("user", beanDefinition);
        System.out.println(beanFactory.getBean("user", User.class));
        System.out.println(beanFactory.getBean("user", User.class, "xiaoming", 12));
    }


    @Test
    public void test3() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.setSimpleStrategy(true);

        BeanDefinition petDefinition = new BeanDefinition(Pet.class);
        PropertyValue p1 = new PropertyValue("name", "dog");
        PropertyValue p2 = new PropertyValue("age", 1);
        PropertyValues ps = new PropertyValues(p1, p2);
        petDefinition.setPropertyValues(ps);
        beanFactory.registerBeanDefinition("pet", petDefinition);

        PropertyValues propertyValues = new PropertyValues();
        PropertyValue pv1 = new PropertyValue("name", "xiao");
        PropertyValue pv2 = new PropertyValue("age", 11);
        PropertyValue pv3 = new PropertyValue("pet", new BeanReference("pet"));
        propertyValues.addPropertyValue(pv1, pv2, pv3);

        BeanDefinition beanDefinition = new BeanDefinition(User.class, propertyValues);
        beanFactory.registerBeanDefinition("user", beanDefinition);


        System.out.println(beanFactory.getBean("user", User.class));

    }

}
