package entity.circle;

public class A {
    private B b;
    private String name;

    public B getB() {
        System.out.println(b);
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return "aaaa";
    }
}
