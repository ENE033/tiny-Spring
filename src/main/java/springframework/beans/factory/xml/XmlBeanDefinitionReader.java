package springframework.beans.factory.xml;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import springframework.beans.BeansException;
import springframework.beans.PropertyValue;
import springframework.beans.PropertyValues;
import springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.config.BeanReference;
import springframework.beans.factory.config.TypedStringValue;
import springframework.beans.factory.support.AbstractBeanDefinitionReader;
import springframework.beans.factory.support.BeanDefinitionRegistry;
import springframework.beans.factory.support.GenericBeanDefinition;
import springframework.context.annotation.ClassPathBeanDefinitionScanner;
import springframework.context.annotation.ScannedGenericBeanDefinition;
import springframework.core.io.Resource;
import springframework.core.io.ResourceLoader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        super(registry, resourceLoader);
    }

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    /**
     * 在实现类重写，获取resource的输入流
     *
     * @param resource
     * @return
     * @throws BeansException
     */
    @Override
    public int loadBeanDefinitions(Resource resource) throws BeansException {
        int count;
        InputStream inputStream;
        try {
            inputStream = resource.getInputStream();
            setCurrentResource(resource);
            count = doLoadBeanDefinitions(inputStream);
        } catch (BeansException e) {
            throw new BeansException(" Load bean definitions fail ", e);
        }
        return count;
    }


    /**
     * 在源码中：
     * 解析xml元素的方法是在DefaultBeanDefinitionDocumentReader类中定义的parseBeanDefinitions方法
     * <p>
     * 解析xml并读取bean和bean的属性
     *
     * @param inputStream
     * @return
     */
    private int doLoadBeanDefinitions(InputStream inputStream) {
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new BeansException(" Xml parse fail ：", e);
        }
        assert document != null;
        Element element = document.getDocumentElement();

        NodeList childNodes = element.getChildNodes();

        int beanSize = 0;

        // 同一个文件下的beanName集合，在同一个xml文件下不允许重复的id
        Set<String> beanNameSet = new HashSet<>();

        for (int i = 0; i < childNodes.getLength(); i++) {
            // 判断元素
            if (!(childNodes.item(i) instanceof Element)) {
                continue;
            }

            // 获取标签的名字
            String nodeName = childNodes.item(i).getNodeName();

            if ("bean".equals(nodeName)) {
                beanSize += resolveBean((Element) childNodes.item(i), beanNameSet);
            } else if ("context:component-scan".equals(nodeName)) {
                beanSize += resolveComponentScan((Element) childNodes.item(i));
            }
        }
        return beanSize;
    }

    private int resolveBean(Element element, Set<String> beanNameSet) {
        //获取bean的id
        String id = element.getAttribute("id");
        if (beanNameSet.contains(id)) {
            throw new BeansException(" Bean id '" + id + "' is already used in this <beans> element" +
                    "Offending resource: class path resource [" + getCurrentResource().getResourceLocation() + "]");
        }
        //获取bean的name
        String name = element.getAttribute("name");
        //获取bean的类名
        String className = element.getAttribute("class");
        //获取bean的初始化方法
        String initMethod = element.getAttribute("init-method");
        //获取bean的销毁方法
        String destroyMethod = element.getAttribute("destroy-method");
        //获取bean的作用域
        String scope = element.getAttribute("scope");

        //获取bean的类对象
        Class<?> clazz;
        try {
            //通过反射获取bean的类对象
            //clazz = Class.forName(className);
            //使用类加载器获取类，不会触发类的静态方法，forName方法会触发类的静态方法
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new BeansException(" Class does not exist ：" + className, e);
        }

        /**
         * 检查是否接口
         */
        if (Modifier.isInterface(clazz.getModifiers())) {
            throw new BeansException(" Failed to load the xmlBeanDefinition of [" + className + "]: Specified class is an interface ");
        }

        /**
         * 检查是否抽象类
         */
        if (Modifier.isAbstract(clazz.getModifiers())) {
            throw new BeansException(" Failed to load the xmlBeanDefinition of [" + className + "]: Is it an abstract class? ");
        }

        //先判断id是否为空，id的优先级比name高
        String beanName = id != null && !id.isEmpty() ? id : name;

        /**
         * id和name都为空则使用简单类名，并将首字母小写，弃用
         */
        //if (beanName == null || "".equals(beanName)) {
        //    beanName = clazz.getSimpleName();
        //    char[] chars = beanName.toCharArray();
        //    chars[0] = Character.toLowerCase(chars[0]);
        //    beanName = new String(chars);
        //}


        //判断beanName是否重复


        if (beanName == null || beanName.isEmpty()) {
            throw new BeansException(" The id of class '" + className + "' is null ");
        }


        //创建beanDefinition
        BeanDefinition xmlBeanDefinition = new GenericBeanDefinition(clazz);

        //注册初始化方法
        xmlBeanDefinition.setInitMethodName(initMethod);
        //注册销毁方法
        xmlBeanDefinition.setDestroyMethodName(destroyMethod);
        //注册bean的作用域
        if (!"".equals(scope)) {
            xmlBeanDefinition.setScope(scope);
        }
        //注册bean的来源
        xmlBeanDefinition.setResource(getCurrentResource());

        for (int j = 0; j < element.getChildNodes().getLength(); j++) {
            //判断元素
            if (!(element.getChildNodes().item(j) instanceof Element)) {
                continue;
            }
            //判断是否属性
            if (!"property".equals(element.getChildNodes().item(j).getNodeName())) {
                continue;
            }
            //解析属性
            Element property = (Element) element.getChildNodes().item(j);
            //获取属性名
            String attrName = property.getAttribute("name");
            //获取属性值
            String attrValue = property.getAttribute("value");
            //获取属性引用
            String attrRef = property.getAttribute("ref");
            //判断属性是值还是引用
            Object value = attrRef != null && !attrRef.isEmpty() ? new BeanReference(attrRef) : new TypedStringValue(attrValue);
            //创建属性
            PropertyValue pv = new PropertyValue(attrName, value);
            //添加属性
            xmlBeanDefinition.getPropertyValues().addPropertyValue(pv);
        }

        //检查beanName是否已经存在，如果存在是否兼容
        if (getRegistry().checkCandidate(beanName, xmlBeanDefinition)) {
            //将beanName加入到当前上下文的beanNameSet中
            beanNameSet.add(beanName);
            //注册到beanDefinition
            getRegistry().registerBeanDefinition(beanName, xmlBeanDefinition);
            return 1;
        } else {
            BeanDefinition existingBeanDefinition = getRegistry().getBeanDefinition(beanName);
            //如果existingBeanDefinition是注解扫描的，那么要合并到xmlBeanDefinition中
            if (existingBeanDefinition instanceof ScannedGenericBeanDefinition) {
                getRegistry().mergeBeanDefinition(xmlBeanDefinition, existingBeanDefinition);
            }
            getRegistry().registerBeanDefinition(beanName, xmlBeanDefinition);
            return 0;
        }
    }

    private int resolveComponentScan(Element component) {
        // 获取需要扫描的包路径
        String attribute = component.getAttribute("base-package");
        // 检查beanDefinitionMap中是否存在autowiredAnnotationProcessor，
        // 如果不存在则需要在beanDefinitionMap中创建一个
        if (!getRegistry().containsBeanDefinition("autowiredAnnotationProcessor")) {
            getRegistry().registerBeanDefinition("autowiredAnnotationProcessor", new GenericBeanDefinition(AutowiredAnnotationBeanPostProcessor.class));
        }
        return scanPackage(attribute);
    }

    public int scanPackage(String path) {
        // 以","作为分隔符分隔包路径
        String[] basePackages = path.split(",");
        ClassPathBeanDefinitionScanner classPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(getRegistry());
        return classPathBeanDefinitionScanner.doScan(basePackages);
    }

}
