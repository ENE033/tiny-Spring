package entity.event;

import springframework.context.ApplicationContext;
import springframework.context.event.ApplicationContextEvent;

public class CustomEvent extends ApplicationContextEvent {
    private int id;
    private String message;

    public CustomEvent(ApplicationContext source, int id, String message) {
        super(source);
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

