package springframework.beans.factory.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import springframework.beans.BeansException;
import springframework.beans.PropertyValue;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.config.BeanReference;
import springframework.beans.factory.support.AbstractBeanDefinitionReader;
import springframework.beans.factory.support.BeanDefinitionRegistry;
import springframework.core.io.Resource;
import springframework.core.io.ResourceLoader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

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
            count = doLoadBeanDefinitions(inputStream);
        } catch (BeansException e) {
            throw new BeansException(" Load bean definitions fail ", e);
        }
        return count;
    }

    /**
     * 解析xml并读取bean和bean的属性
     *
     * @param inputStream
     * @return
     */
    public int doLoadBeanDefinitions(InputStream inputStream) {
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new BeansException(" Xml parse fail ", e);
        }
        assert document != null;
        Element element = document.getDocumentElement();

        NodeList childNodes = element.getChildNodes();

        int beanSize = 0;

        for (int i = 0; i < childNodes.getLength(); i++) {
            //判断元素
            if (!(childNodes.item(i) instanceof Element)) {
                continue;
            }
            //判断bean对象
            if (!"bean".equals(childNodes.item(i).getNodeName())) {
                continue;
            }
            //解析标签
            Element bean = (Element) childNodes.item(i);
            //获取bean的id
            String id = bean.getAttribute("id");
            //获取bean的name
            String name = bean.getAttribute("name");
            //获取bean的类名
            String className = bean.getAttribute("class");
            //获取bean的初始化方法
            String initMethod = bean.getAttribute("init-method");
            //获取bean的销毁方法
            String destroyMethod = bean.getAttribute("destroy-method");
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
            //先判断id是否为空，id的优先级比name高
            String beanName = id != null && !id.isEmpty() ? id : name;

            //id和name都为空则使用简单类名，并将首字母小写
            if (beanName == null || "".equals(beanName)) {
                beanName = clazz.getSimpleName();
                char[] chars = beanName.toCharArray();
                chars[0] = Character.toLowerCase(chars[0]);
                beanName = new String(chars);
            }

            //判断beanName是否重复
            if (getRegistry().containsBeanDefinition(beanName)) {
                throw new BeansException(" Duplicate beanName [" + beanName + "] is not allowed ");
            }

            //创建beanDefinition
            BeanDefinition beanDefinition = new BeanDefinition(clazz);

            //设置初始化方法
            beanDefinition.setInitMethodName(initMethod);
            //设置销毁方法
            beanDefinition.setDestroyMethodName(destroyMethod);

            for (int j = 0; j < bean.getChildNodes().getLength(); j++) {
                //判断元素
                if (!(bean.getChildNodes().item(j) instanceof Element)) {
                    continue;
                }
                //判断是否属性
                if (!"property".equals(bean.getChildNodes().item(j).getNodeName())) {
                    continue;
                }
                //解析属性
                Element property = (Element) bean.getChildNodes().item(j);
                //获取属性名
                String attrName = property.getAttribute("name");
                //获取属性值
                String attrValue = property.getAttribute("value");
                //获取属性引用
                String attrRef = property.getAttribute("ref");
                //判断属性是值还是引用
                Object value = attrRef != null && !attrRef.isEmpty() ? new BeanReference(attrRef) : attrValue;
                //创建属性
                PropertyValue pv = new PropertyValue(attrName, value);
                //添加属性
                beanDefinition.getPropertyValues().addPropertyValue(pv);
            }

            //注册到beanDefinition
            getRegistry().registerBeanDefinition(beanName, beanDefinition);
            beanSize++;
        }
        return beanSize;
    }

}
