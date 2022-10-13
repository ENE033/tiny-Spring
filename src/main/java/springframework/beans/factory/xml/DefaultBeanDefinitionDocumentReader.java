package springframework.beans.factory.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.config.BeanDefinitionHolder;


public class DefaultBeanDefinitionDocumentReader {

    public static final String BEAN_ELEMENT = BeanDefinitionParserDelegate.BEAN_ELEMENT;

    public static final String NESTED_BEANS_ELEMENT = "beans";

    public static final String ALIAS_ELEMENT = "alias";

    public static final String NAME_ATTRIBUTE = "name";

    public static final String ALIAS_ATTRIBUTE = "alias";

    public static final String IMPORT_ELEMENT = "import";

    public static final String RESOURCE_ATTRIBUTE = "resource";

    public static final String PROFILE_ATTRIBUTE = "profile";

    // 解析器代表
    public BeanDefinitionParserDelegate delegate;

    // 用于获取当前的容器
    public XmlBeanDefinitionReader reader;

    public DefaultBeanDefinitionDocumentReader() {
    }

    // 注入reader
    public DefaultBeanDefinitionDocumentReader(XmlBeanDefinitionReader reader) {
        this.reader = reader;
    }

    /**
     * 从document解析出beanDefinition
     *
     * @param document
     */
    public void registerBeanDefinitions(Document document) {
        // document.getDocumentElement()获得的是<beans></beans>这个元素
        doRegisterBeanDefinitions(document.getDocumentElement());
    }

    protected void doRegisterBeanDefinitions(Element root) {
        // 创建解析器代表
        // 每次解析一个资源（一个xml文件）就新建一个创建解析器代表
        // 用于存放每个xml中的独立的信息
        // 例如：当一个xml中有两个相同的id时，会报错，而两个xml中存在相同的id时，则会覆盖，这是其实现的原理
        this.delegate = new BeanDefinitionParserDelegate(reader);
        parseBeanDefinitions(root, this.delegate);
    }

    protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
        // 获取root元素（<beans></beans>）中的子节点
        NodeList childNodes = root.getChildNodes();
        // 遍历子节点
        for (int i = 0; i < childNodes.getLength(); i++) {
            // 判断是否元素
            if (!(childNodes.item(i) instanceof Element)) {
                continue;
            }
            // 获取当前元素
            Element ele = (Element) childNodes.item(i);
            // 如果当前元素是默认命名空间的
            if (delegate.isDefaultNamespace(ele)) {
                parseDefaultElement(ele, delegate);
            }
            // 如果当前元素是自定义命名空间的
            else {
                delegate.parseCustomElement(ele);
            }
        }
    }

    /**
     * 解析默认命名空间的元素
     *
     * @param element
     * @param delegate
     */
    protected void parseDefaultElement(Element element, BeanDefinitionParserDelegate delegate) {
        // 如果该元素是bean
        if (BEAN_ELEMENT.equals(element.getNodeName())) {
            processBeanDefinition(element, delegate);
        }
        // 在源码中：
        // 还存在IMPORT_ELEMENT、ALIAS_ELEMENT、NESTED_BEANS_ELEMENT类型的元素
    }

    /**
     * 解析bean的属性及其子标签中的信息，并注册到beanDefinition中
     *
     * @param element
     * @param delegate
     */
    protected void processBeanDefinition(Element element, BeanDefinitionParserDelegate delegate) {
        // 解析bean的属性及其子标签中的信息
        BeanDefinitionHolder beanDefinitionHolder = delegate.parseBeanDefinitionElement(element);
        // 注册到beanDefinition中
        if (beanDefinitionHolder != null) {
            reader.getRegistry().registerBeanDefinition(beanDefinitionHolder.getBeanName(), beanDefinitionHolder.getBeanDefinition());
        }
    }


}
