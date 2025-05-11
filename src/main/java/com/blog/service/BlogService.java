package com.blog.service;

import com.blog.entity.Blog;
import java.util.List;

public interface BlogService {
    Blog saveBlog(Blog blog);
    Blog getBlogById(Long id);
    List<Blog> getAllBlogs();
    List<Blog> searchBlogsByKeyword(String keyword);
    void deleteBlog(Long id);
}
