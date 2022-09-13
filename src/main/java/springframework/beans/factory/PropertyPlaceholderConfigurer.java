package springframework.beans.factory;

import springframework.beans.BeansException;
import springframework.beans.PropertyValue;
import springframework.beans.factory.config.BeanDefinition;
import springframework.beans.factory.config.BeanFactoryPostProcessor;
import springframework.beans.factory.config.ConfigurableListableBeanFactory;
import springframework.beans.factory.config.TypedStringValue;
import springframework.core.io.DefaultResourceLoader;
import springframework.core.io.Resource;
import springframework.core.io.ResourceLoader;
import springframework.util.StringValueResolver;

import java.util.Properties;

public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";
    private String location;

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //获取资源加载器
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        //获取资源
        Resource resource = resourceLoader.getResource(location);
        //创建Properties对象
        Properties properties = new Properties();
        try {
            //properties加载资源字节流
            properties.load(resource.getInputStream());
        } catch (Exception e) {
            throw new BeansException(" Properties failed to load ", e);
        }
        //遍历所有的beanDefinition
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            //获取beanDefinition
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            //遍历beanDefinition中的全部属性
            for (PropertyValue propertyValue : beanDefinition.getPropertyValues().getPropertyValues()) {
                //获取对应属性的值，(BeanReference或者TypedStringValue)
                Object value = propertyValue.getValue();
                //如果不是TypedStringValue，则continue
                if (!(value instanceof TypedStringValue)) {
                    continue;
                }
                //强转
                TypedStringValue typedStringValue = (TypedStringValue) value;
                typedStringValue.setValue(resolvePlaceholder(typedStringValue.getValue(), properties));
            }
        }
        //将字符解析器加入到容器中，提供@Value注解使用
        PlaceholderResolvingStringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(properties);
        beanFactory.addEmbeddedValueResolver(valueResolver);
    }

    /**
     * 解析value在properties中的值
     *
     * @param value
     * @param properties
     * @return
     */
    public String resolvePlaceholder(String value, Properties properties) {
        //构造stringBuilder并解析判断
        StringBuilder strVal = new StringBuilder(value);
        int start = strVal.indexOf(DEFAULT_PLACEHOLDER_PREFIX);
        int end = strVal.lastIndexOf(DEFAULT_PLACEHOLDER_SUFFIX);
        //${需要在第一位，}需要在最后一位，start要小于end
        if (start == 0 && end == strVal.length() - 1 && start < end) {
            //获取占位符中的内容
            String val = strVal.substring(start + 2, end);
            //在properties中获取占位符的值，如果键为空，那么值为原来的值
            return properties.getProperty(val, value);
        }
        //无法解析则直接返回原本的值
        return value;
    }

    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

        private final Properties properties;

        public PlaceholderResolvingStringValueResolver(Properties properties) {
            this.properties = properties;
        }

        @Override
        public String resolveStringValue(String strVal) {
            return PropertyPlaceholderConfigurer.this.resolvePlaceholder(strVal, properties);
        }
    }

}
