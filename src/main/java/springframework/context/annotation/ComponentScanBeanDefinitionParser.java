package springframework.context.annotation;

import org.w3c.dom.Element;
import springframework.beans.BeansException;
import springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.support.BeanDefinitionRegistry;
import springframework.beans.factory.support.GenericBeanDefinition;
import springframework.beans.factory.xml.ParserContext;

public class ComponentScanBeanDefinitionParser implements BeanDefinitionParser {
    private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";

    public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME =
            "internalAutowiredAnnotationProcessor";

    /**
     * 解析@Component注解的beanDefinition
     *
     * @param element
     * @param parserContext
     * @return
     */
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        if (element.hasAttribute(BASE_PACKAGE_ATTRIBUTE)) {
            String str = element.getAttribute(BASE_PACKAGE_ATTRIBUTE);
            String[] basePackages = str.split(",");
            // 获取registry并注入scanner中，是scanner扫描得到的bean能注册到registry中
            BeanDefinitionRegistry registry = parserContext.getXmlBeanDefinitionReader().getRegistry();
            ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);

            // 扫描包下的bean并注册到beanDefinitionMap中
            scanner.doScan(basePackages);

            // 注册internalAutowiredAnnotationProcessor的beanDefinition
            if (!registry.containsBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
                registry.registerBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME,
                        new GenericBeanDefinition(AutowiredAnnotationBeanPostProcessor.class));
            }

        } else {
            // 源码中使用了断言Assert.notEmpty(basePackages, "At least one base package must be specified");
            throw new BeansException(" At least one base package must be specified ");
        }
        return null;
    }
}
