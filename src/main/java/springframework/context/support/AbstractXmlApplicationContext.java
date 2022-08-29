package springframework.context.support;

import springframework.beans.factory.support.DefaultListableBeanFactory;
import springframework.beans.factory.xml.XmlBeanDefinitionReader;
import springframework.core.io.Resource;

public abstract class AbstractXmlApplicationContext extends AbstractRefreshableApplicationContext {

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        //使用 XmlBeanDefinitionReader 类，处理了关于 XML 文件配置信息的操作
        //本类实现了ResourceLoader接口
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory, this);
        Resource[] configLocations = getConfigLocations();
        if (null != configLocations) {
            xmlBeanDefinitionReader.loadBeanDefinitions(configLocations);
        }
    }

    /**
     * 获取需要加载的路径
     *
     * @return
     */
    protected abstract Resource[] getConfigLocations();

}
