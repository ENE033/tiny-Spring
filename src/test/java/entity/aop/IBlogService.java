package entity.aop;

import java.time.LocalDate;

public interface IBlogService {
    Blog getBlog();

    Long getBlogID(Blog blog);

    LocalDate getBlogDate(Blog blog);

    String getBlogContext(Blog blog);

}
