package springframework.context.annotation;

import org.w3c.dom.Element;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.xml.ParserContext;

/**
 * BeanDefinition解析器
 */
public interface BeanDefinitionParser {

    BeanDefinition parse(Element element, ParserContext parserContext);

}
