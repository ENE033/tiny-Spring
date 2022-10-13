package springframework.beans.factory.support;

import springframework.beans.BeansException;
import springframework.core.io.DefaultResourceLoader;
import springframework.core.io.Resource;
import springframework.core.io.ResourceLoader;

public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

    private final BeanDefinitionRegistry registry;

    private final ResourceLoader resourceLoader;

    private Resource currentResource;


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


    public Resource getCurrentResource() {
        return currentResource;
    }

    public void setCurrentResource(Resource resource) {
        this.currentResource = resource;
    }

    /**
     * 在抽象类重写，重复调用loadBeanDefinitions(Resource resource)
     *
     * @param resources
     * @return
     * @throws BeansException
     */
    @Override
    public void loadBeanDefinitions(Resource... resources) throws BeansException {
        for (Resource resource : resources) {
            loadBeanDefinitions(resource);
        }
    }

    /**
     * 在抽象类重写，重复调用loadBeanDefinitions(String location)
     *
     * @param locations
     * @return
     * @throws BeansException
     */
    @Override
    public void loadBeanDefinitions(String... locations) throws BeansException {
        for (String location : locations) {
            loadBeanDefinitions(location);
        }
    }

    /**
     * 在抽象类重写，重复调用loadBeanDefinitions(Resource resource)
     *
     * @param location
     * @return
     * @throws BeansException
     */
    @Override
    public void loadBeanDefinitions(String location) throws BeansException {
        ResourceLoader resourceLoader = getResourceLoader();
        currentResource = resourceLoader.getResource(location);
        loadBeanDefinitions(currentResource);
    }
}
