package entity.aop;

import java.time.LocalDate;

public class Blog {
    private Long ID;
    private String context;
    private LocalDate createDate;

    public Blog() {
    }

    public Blog(Long ID, String context, LocalDate createDate) {
        this.ID = ID;
        this.context = context;
        this.createDate = createDate;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "ID=" + ID +
                ", context='" + context + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
