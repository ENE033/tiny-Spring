package springframework.beans.factory.support;

import springframework.beans.BeansException;
import springframework.core.io.DefaultResourceLoader;
import springframework.core.io.Resource;
import springframework.core.io.ResourceLoader;

public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

    private final BeanDefinitionRegistry registry;

    private final ResourceLoader resourceLoader;


    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        this.registry = registry;
        this.resourceLoader = resourceLoader;
    }

    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this(registry, new DefaultResourceLoader());
    }

    @Override
    public BeanDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    /**
     * 在抽象类重写，重复调用loadBeanDefinitions(Resource resource)
     *
     * @param resources
     * @return
     * @throws BeansException
     */
    @Override
    public int loadBeanDefinitions(Resource... resources) throws BeansException {
        int count = 0;
        for (Resource resource : resources) {
            count += loadBeanDefinitions(resource);
        }
        return count;
    }

    /**
     * 在抽象类重写，重复调用loadBeanDefinitions(String location)
     *
     * @param locations
     * @return
     * @throws BeansException
     */
    @Override
    public int loadBeanDefinitions(String... locations) throws BeansException {
        int count = 0;
        for (String location : locations) {
            count += loadBeanDefinitions(location);
        }
        return count;
    }

    /**
     * 在抽象类重写，重复调用loadBeanDefinitions(Resource resource)
     *
     * @param location
     * @return
     * @throws BeansException
     */
    @Override
    public int loadBeanDefinitions(String location) throws BeansException {
        ResourceLoader resourceLoader = getResourceLoader();
        Resource resource = resourceLoader.getResource(location);
        return loadBeanDefinitions(resource);
    }
}
