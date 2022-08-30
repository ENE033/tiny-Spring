package springframework.context.support;

import springframework.beans.BeansException;
import springframework.core.io.ClassPathResource;
import springframework.core.io.Resource;

public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {


    private Resource[] configResources;

    public ClassPathXmlApplicationContext() {
    }

    public ClassPathXmlApplicationContext(String path) {
        this(new String[]{path});
    }

    public ClassPathXmlApplicationContext(String... paths) {
        this.configResources = new Resource[paths.length];
        for (int i = 0; i < paths.length; i++) {
            configResources[i] = new ClassPathResource(paths[i]);
        }

        /**
         * 刷新beanFactory
         */
        refresh();
    }


    @Override
    protected Resource[] getConfigLocations() {
        return this.configResources;
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return getBeanFactory().getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requireType) throws BeansException {
        return getBeanFactory().getBean(name, requireType);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return getBeanFactory().getBean(name, args);
    }

    @Override
    public <T> T getBean(String name, Class<T> requireType, Object... args) throws BeansException {
        return getBeanFactory().getBean(name, requireType, args);
    }
}
