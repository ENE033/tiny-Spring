package springframework.context;

import java.util.EventObject;

public abstract class ApplicationEvent extends EventObject {
    private final long timestamp;

    public ApplicationEvent(ApplicationContext source) {
        super(source);
        timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
}
