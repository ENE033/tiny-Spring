package entity.aop;

import java.time.LocalDate;

public class BlogService implements IBlogService {
    @Override
    public Blog getBlog() {
        return new Blog();
    }

    @Override
    public Long getBlogID(Blog blog) {
        return blog.getID();
    }

    @Override
    public LocalDate getBlogDate(Blog blog) {
        return blog.getCreateDate();
    }

    @Override
    public String getBlogContext(Blog blog) {
        return blog.getContext();
    }
}
