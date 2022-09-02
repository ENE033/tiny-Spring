import entity.*;
import org.junit.Test;
import springframework.beans.BeanUtils;
import springframework.beans.BeansException;
import springframework.beans.PropertyValue;
import springframework.beans.PropertyValues;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.config.BeanReference;
import springframework.beans.factory.support.DefaultListableBeanFactory;
import springframework.beans.factory.xml.XmlBeanDefinitionReader;
import springframework.context.ApplicationContext;
import springframework.context.support.AbstractApplicationContext;
import springframework.context.support.AbstractRefreshableApplicationContext;
import springframework.context.support.ClassPathXmlApplicationContext;
import springframework.core.io.Resource;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;


public class test {

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

    @Test
    public void test4() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        Resource resource = xmlBeanDefinitionReader.getResourceLoader().getResource("classpath:xml.xml");
//        Resource resource = xmlBeanDefinitionReader.getResourceLoader().getResource("D:\\java_workspace\\Spring\\src\\main\\resources\\xml.xml");
//        Resource resource = xmlBeanDefinitionReader.getResourceLoader().getResource("http://www.woaiguozhi.top/pan/Files/xml.xml");
        int i = xmlBeanDefinitionReader.loadBeanDefinitions(resource);
        User user = beanFactory.getBean("user", User.class, "xiaoming", 12, new Pet("str", 12));
        System.out.println(user);
        System.out.println(user.getClass().getSuperclass());
    }


    @Test
    public void test5() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("xml.xml");
        User user = context.getBean("user", User.class);
        System.out.println(user);
    }

    @Test
    public void test6() {
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("xml.xml");
        context.registerShutdownHook();
        UserDao userDao = context.getBean("userDao", UserDao.class);
        System.out.println(userDao.getClass());
        System.out.println(((Object) userDao).getClass());
        System.out.println(BeanUtils.findDeclaredMethod(UserDao.class, "initMethod"));
    }

    @Test
    public void test7() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("xml.xml");
        System.out.println(context);
        KnownAll knownAll = context.getBean("knownAll", KnownAll.class);
        System.out.println(knownAll.getClassLoader());
        System.out.println(knownAll.getApplicationContext());
        System.out.println(knownAll.getBeanName());
        System.out.println(knownAll.getBeanFactory());
//        for (Method declaredMethod : AbstractRefreshableApplicationContext.class.getDeclaredMethods()) {
//            System.out.println(declaredMethod);
//        }
        try {
            System.out.println(AbstractRefreshableApplicationContext.class.getDeclaredMethod("createBeanFactory"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test8() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("xml.xml");
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
//        System.out.println(beanFactory.getSingletonObjects().entrySet());
//        System.out.println(context.getBean("pet"));
//        System.out.println(context.getBean("pet"));
//        User user = context.getBean("user", User.class);
//        User user1 = context.getBean("user", User.class);
//        System.out.println(user);
//        System.out.println(user1);

    }


    @Test
    public void test9() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("xml.xml");
        PetService petService = context.getBean("petService", PetService.class);
        IPetDao iPetDao = petService.getiPetDao();
        System.out.println(iPetDao.queryPetName("12388"));
    }
}
