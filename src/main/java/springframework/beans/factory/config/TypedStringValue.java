package springframework.beans.factory.config;

public class TypedStringValue {

    private String value;

    private Object targetClass;


    public TypedStringValue() {
    }

    public TypedStringValue(String value) {
        this.value = value;
    }

    public TypedStringValue(String value, Object targetClass) {
        this.value = value;
        this.targetClass = targetClass;
    }

    public Object getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
