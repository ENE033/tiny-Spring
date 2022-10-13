package springframework.beans.factory.xml;

/**
 * 解析器上下文，用于保存当前的解析器代表和当前的reader上下文
 */
public class ParserContext {
    private final XmlBeanDefinitionReader xmlBeanDefinitionReader;

    private final BeanDefinitionParserDelegate beanDefinitionParserDelegate;


    public ParserContext(XmlBeanDefinitionReader xmlBeanDefinitionReader, BeanDefinitionParserDelegate beanDefinitionParserDelegate) {
        this.xmlBeanDefinitionReader = xmlBeanDefinitionReader;
        this.beanDefinitionParserDelegate = beanDefinitionParserDelegate;
    }

    public XmlBeanDefinitionReader getXmlBeanDefinitionReader() {
        return xmlBeanDefinitionReader;
    }

    public BeanDefinitionParserDelegate getBeanDefinitionParserDelegate() {
        return beanDefinitionParserDelegate;
    }
}
