package springframework.beans.factory.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import springframework.beans.BeansException;
import springframework.beans.PropertyValue;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.config.BeanDefinitionHolder;
import springframework.beans.factory.config.BeanReference;
import springframework.beans.factory.config.TypedStringValue;
import springframework.beans.factory.support.BeanDefinitionRegistry;
import springframework.beans.factory.support.GenericBeanDefinition;
import springframework.context.annotation.ComponentScanBeanDefinitionParser;

import java.lang.reflect.Modifier;
import java.util.*;

public class BeanDefinitionParserDelegate {

    public static final String BEANS_NAMESPACE_URI = "http://www.springframework.org/schema/beans";

    public static final String MULTI_VALUE_ATTRIBUTE_DELIMITERS = ",; ";

    public static final String TRUE_VALUE = "true";

    public static final String FALSE_VALUE = "false";

    public static final String DEFAULT_VALUE = "default";

    public static final String DESCRIPTION_ELEMENT = "description";

    public static final String AUTOWIRE_NO_VALUE = "no";

    public static final String AUTOWIRE_BY_NAME_VALUE = "byName";

    public static final String AUTOWIRE_BY_TYPE_VALUE = "byType";

    public static final String AUTOWIRE_CONSTRUCTOR_VALUE = "constructor";

    public static final String AUTOWIRE_AUTODETECT_VALUE = "autodetect";

    public static final String NAME_ATTRIBUTE = "name";

    public static final String BEAN_ELEMENT = "bean";

    public static final String META_ELEMENT = "meta";

    public static final String ID_ATTRIBUTE = "id";

    public static final String PARENT_ATTRIBUTE = "parent";

    public static final String CLASS_ATTRIBUTE = "class";

    public static final String ABSTRACT_ATTRIBUTE = "abstract";

    public static final String SCOPE_ATTRIBUTE = "scope";

    private static final String SINGLETON_ATTRIBUTE = "singleton";

    public static final String LAZY_INIT_ATTRIBUTE = "lazy-init";

    public static final String AUTOWIRE_ATTRIBUTE = "autowire";

    public static final String AUTOWIRE_CANDIDATE_ATTRIBUTE = "autowire-candidate";

    public static final String PRIMARY_ATTRIBUTE = "primary";

    public static final String DEPENDS_ON_ATTRIBUTE = "depends-on";

    public static final String INIT_METHOD_ATTRIBUTE = "init-method";

    public static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";

    public static final String FACTORY_METHOD_ATTRIBUTE = "factory-method";

    public static final String FACTORY_BEAN_ATTRIBUTE = "factory-bean";

    public static final String CONSTRUCTOR_ARG_ELEMENT = "constructor-arg";

    public static final String INDEX_ATTRIBUTE = "index";

    public static final String TYPE_ATTRIBUTE = "type";

    public static final String VALUE_TYPE_ATTRIBUTE = "value-type";

    public static final String KEY_TYPE_ATTRIBUTE = "key-type";

    public static final String PROPERTY_ELEMENT = "property";

    public static final String REF_ATTRIBUTE = "ref";

    public static final String VALUE_ATTRIBUTE = "value";

    public static final String LOOKUP_METHOD_ELEMENT = "lookup-method";

    public static final String REPLACED_METHOD_ELEMENT = "replaced-method";

    public static final String REPLACER_ATTRIBUTE = "replacer";

    public static final String ARG_TYPE_ELEMENT = "arg-type";

    public static final String ARG_TYPE_MATCH_ATTRIBUTE = "match";

    public static final String REF_ELEMENT = "ref";

    public static final String IDREF_ELEMENT = "idref";

    public static final String BEAN_REF_ATTRIBUTE = "bean";

    public static final String PARENT_REF_ATTRIBUTE = "parent";

    public static final String VALUE_ELEMENT = "value";

    public static final String NULL_ELEMENT = "null";

    public static final String ARRAY_ELEMENT = "array";

    public static final String LIST_ELEMENT = "list";

    public static final String SET_ELEMENT = "set";

    public static final String MAP_ELEMENT = "map";

    public static final String ENTRY_ELEMENT = "entry";

    public static final String KEY_ELEMENT = "key";

    public static final String KEY_ATTRIBUTE = "key";

    public static final String KEY_REF_ATTRIBUTE = "key-ref";

    public static final String VALUE_REF_ATTRIBUTE = "value-ref";

    public static final String PROPS_ELEMENT = "props";

    public static final String PROP_ELEMENT = "prop";

    public static final String MERGE_ATTRIBUTE = "merge";

    public static final String QUALIFIER_ELEMENT = "qualifier";

    public static final String QUALIFIER_ATTRIBUTE_ELEMENT = "attribute";

    public static final String DEFAULT_LAZY_INIT_ATTRIBUTE = "default-lazy-init";

    public static final String DEFAULT_MERGE_ATTRIBUTE = "default-merge";

    public static final String DEFAULT_AUTOWIRE_ATTRIBUTE = "default-autowire";

    public static final String DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE = "default-autowire-candidates";

    public static final String DEFAULT_INIT_METHOD_ATTRIBUTE = "default-init-method";

    public static final String DEFAULT_DESTROY_METHOD_ATTRIBUTE = "default-destroy-method";

    // 用于给解析器代表感知当前的上下文中的beanFactory
    public final XmlBeanDefinitionReader xmlBeanDefinitionReader;

    // 每个xml中独立的属性
    // 同一个文件下的beanName集合，在同一个xml文件下不允许重复的id
    Set<String> usedNames = new HashSet<>();

    public BeanDefinitionParserDelegate(XmlBeanDefinitionReader xmlBeanDefinitionReader) {
        this.xmlBeanDefinitionReader = xmlBeanDefinitionReader;
    }

    /**
     * 判断是否默认的命名空间，这里直接使用了节点名是否等于bean来判断
     *
     * @param element
     * @return
     */
    public boolean isDefaultNamespace(Node element) {
        return BEAN_ELEMENT.equals(element.getNodeName());
//        return BEANS_NAMESPACE_URI.equals(element.getNamespaceURI());
    }

    /**
     * 处理非默认命名空间的元素
     *
     * @param ele
     */
    public void parseCustomElement(Element ele) {
        ComponentScanBeanDefinitionParser componentScanBeanDefinitionParser = new ComponentScanBeanDefinitionParser();
        componentScanBeanDefinitionParser.parse(ele, new ParserContext(xmlBeanDefinitionReader, this));
    }

    /**
     * 解析单个bean元素，返回值是BeanDefinitionHolder
     *
     * @param element
     * @return
     */
    public BeanDefinitionHolder parseBeanDefinitionElement(Element element) {
        // 解析id
        String id = element.getAttribute(ID_ATTRIBUTE);
        // 解析name
        String nameAttr = element.getAttribute(NAME_ATTRIBUTE);
        // 分隔name
        List<String> names = new ArrayList<>(Arrays.asList(nameAttr.split(",")));

        String beanName = id;
        // 如果id为空，但是name不为空，可以使用第一个别名来作为id，剩下的别名还是别名
        if (beanName.isEmpty() && !names.isEmpty()) {
            beanName = names.remove(0);
            // logs: No XML 'id' specified - using beanName as bean name and aliases as aliases
        }

        // 检查beanName是否在当前xml中重复
        checkNameUniqueness(beanName);

        // 解析当前元素
        BeanDefinition beanDefinition = parseBeanDefinitionElement(element, beanName);
        // 如果beanDefinition不为空
        if (beanDefinition != null) {
            // 判断id是否仍然为空（即xml中没有写明id和name时）
            if (beanName.isEmpty()) {
                // 此时并不会报错，而是会根据类名生成一个独一无二的id
                beanName = generateBeanName(beanDefinition);
            }
            return new BeanDefinitionHolder(beanName, beanDefinition);
        }
        return null;
    }

    /**
     * 根据类名生成一个独一无二的id
     *
     * @param beanDefinition
     * @return
     */
    private String generateBeanName(BeanDefinition beanDefinition) {
        // 获取当前的beanFactory
        BeanDefinitionRegistry registry = this.xmlBeanDefinitionReader.getRegistry();
        // 获取类
        Class<?> beanClass = beanDefinition.getBeanClass();
        // 如果类为空，即xml中没有指定class属性且没有指定id和name的时候，会在这个地方报错
        // 如果指定了id或者name，则会在实例化的时候报错
        if (beanClass == null) {
            throw new BeansException("Unnamed bean definition specifies neither " +
                    "'class' nor 'parent' nor 'factory-bean' - can't generate bean name");
        }
        // 获取类名
        String className = beanClass.getName();
        // 分隔符为#
        String prefix = className + "#";
        int i = 0;
        // i从零开始从一遍历，直到找到当前的bean的独一无二的id为止
        while (registry.containsBeanDefinition(prefix + i)) {
            i++;
        }
        return prefix + i;
    }

    /**
     * 检查beanName是否在当前xml中重复
     *
     * @param beanName
     */
    protected void checkNameUniqueness(String beanName) {
        // 这个usedNames的作用域仅仅在当前的解析器代表，而解析器代表是一个xml一个的
        if (!usedNames.contains(beanName)) {
            // 注意必须要在beanName不为""的时候才把其放入usedNames中
            // 因为不指定id和name的话，beanName默认就是""
            if (!beanName.isEmpty()) {
                usedNames.add(beanName);
            }
        } else {
            throw new BeansException(" Bean id '" + beanName + "' is already used in this <beans> element" +
                    "Offending resource: class path resource [" + this.xmlBeanDefinitionReader.getCurrentResource().getResourceLocation() + "]");
        }
    }

    /**
     * 解析单个bean元素，返回值是BeanDefinition
     *
     * @param ele
     * @param beanName
     * @return
     */
    public BeanDefinition parseBeanDefinitionElement(Element ele, String beanName) {
        // 获取类名
        String className = null;
        if (ele.hasAttribute(CLASS_ATTRIBUTE)) {
            className = ele.getAttribute(CLASS_ATTRIBUTE).trim();
        }
        try {
            // 创建beanDefinition
            BeanDefinition beanDefinition = new GenericBeanDefinition();
            if (className != null) {
                // 通过反射创建clazz，不初始化，既不会执行静态代码块
                Class<?> clazz = Class.forName(className, false, ClassLoader.getSystemClassLoader());
                // 检查是否接口
                if (Modifier.isInterface(clazz.getModifiers())) {
                    throw new BeansException(" Failed to load the xmlBeanDefinition of [" + className + "]: Specified class is an interface ");
                }
                // 检查是否抽象类
                if (Modifier.isAbstract(clazz.getModifiers())) {
                    throw new BeansException(" Failed to load the xmlBeanDefinition of [" + className + "]: Is it an abstract class? ");
                }
                //
                beanDefinition.setBeanClass(clazz);
            }
            // 如果类名是空的，即没有在xml中指定class，那么在这个地方会直接抛出异常
            // 但是在源码中，这个问题是要等到实例化的时候才会抛出异常
            // 也就是说一个没有指定class的beanDefinition是可以一直被注册到beanDefinitionMap中的，
            // 但是这种bean是不能被实例化出来的，少了class对象是不可能实例化出来的，到实例化的时候才会报错
            else {
                throw new BeansException(" No bean class specified on bean definition ");
            }
            // 解析bean的初始化方法，销毁方法，作用域
            parseBeanDefinitionAttributes(ele, beanDefinition);
            // 解析bean的property子标签
            parsePropertyElements(ele, beanDefinition);
            return beanDefinition;
        } catch (ClassNotFoundException e) {
            throw new BeansException(" Class does not exist ：" + className, e);
        }
    }

    /**
     * 解析bean的初始化方法，销毁方法，作用域
     *
     * @param element
     * @param beanDefinition
     * @return
     */
    public BeanDefinition parseBeanDefinitionAttributes(Element element, BeanDefinition beanDefinition) {
        // 获取bean的初始化方法
        if (element.hasAttribute(INIT_METHOD_ATTRIBUTE)) {
            beanDefinition.setInitMethodName(element.getAttribute(INIT_METHOD_ATTRIBUTE));
        }

        // 获取bean的销毁方法
        if (element.hasAttribute(DESTROY_METHOD_ATTRIBUTE)) {
            beanDefinition.setDestroyMethodName(element.getAttribute(DESTROY_METHOD_ATTRIBUTE));
        }

        // 获取bean的作用域
        if (element.hasAttribute(SCOPE_ATTRIBUTE)) {
            beanDefinition.setScope(element.getAttribute(SCOPE_ATTRIBUTE));
        } else {
            beanDefinition.setScope(SINGLETON_ATTRIBUTE);
        }

        return beanDefinition;
    }

    /**
     * 解析bean的property子标签
     *
     * @param beanElement
     * @param beanDefinition
     */
    public void parsePropertyElements(Element beanElement, BeanDefinition beanDefinition) {
        NodeList childNodes = beanElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            // 当node是Element且node的名字是property
            if (node instanceof Element && PROPERTY_ELEMENT.equals(node.getNodeName())) {
                parsePropertyElement((Element) node, beanDefinition);
            }
        }

    }


    /**
     * 解析单个bean的property子标签
     *
     * @param ele
     * @param beanDefinition
     */
    public void parsePropertyElement(Element ele, BeanDefinition beanDefinition) {
        // 属性名
        String name = ele.getAttribute(NAME_ATTRIBUTE);
        // 属性值
        Object value = parsePropertyValue(ele, beanDefinition, name);
        // 放入属性集
        beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, value));
    }


    /**
     * 解析属性值
     *
     * @param ele
     * @param bd
     * @param propertyName
     * @return
     */
    public Object parsePropertyValue(Element ele, BeanDefinition bd, String propertyName) {
        String elementName = (propertyName != null ?
                "<property> element for property '" + propertyName + "'" :
                "<constructor-arg> element");
        // 是否存在引用数据类型
        boolean hasRefAttribute = ele.hasAttribute(REF_ATTRIBUTE);
        // 是否存在基本数据类型
        boolean hasValueAttribute = ele.hasAttribute(VALUE_ATTRIBUTE);
        // 不能同时存在ref和value
        if (hasRefAttribute && hasValueAttribute) {
            throw new BeansException(elementName + " is only allowed to contain either 'ref' attribute OR 'value' attribute ");
        }
        if (hasRefAttribute) {
            String ref = ele.getAttribute(REF_ATTRIBUTE);
            // ref不能为空
            if ("".equals(ref.trim())) {
                throw new BeansException(elementName + " contains empty 'ref' attribute");
            }
            return new BeanReference(ref);
        } else {
            String value = ele.getAttribute(VALUE_ATTRIBUTE);
            return new TypedStringValue(value);
        }
    }


}
