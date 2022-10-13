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
    // 创建DefaultBeanDefinitionDocumentReader对象，
    // 全局（整个context）只有一个DefaultBeanDefinitionDocumentReader
    // 将当前的reader作为参数传入，用于传入当前上下文的beanDefinitionRegistry
    // 用于解析xml中的beanDefinition
    DefaultBeanDefinitionDocumentReader beanDefinitionDocumentReader = new DefaultBeanDefinitionDocumentReader(this);

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
    public void loadBeanDefinitions(Resource resource) throws BeansException {
        InputStream inputStream;
        try {
            inputStream = resource.getInputStream();
            setCurrentResource(resource);
            doLoadBeanDefinitions(inputStream);
        } catch (BeansException e) {
            throw new BeansException(" Load bean definitions fail ", e);
        }
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
    private void doLoadBeanDefinitions(InputStream inputStream) {
        Document document;
        try {
            // 解析xml资源流成document对象
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new BeansException(" Xml parse fail ：", e);
        }
        // 注入document对象
        beanDefinitionDocumentReader.registerBeanDefinitions(document);
    }

}
