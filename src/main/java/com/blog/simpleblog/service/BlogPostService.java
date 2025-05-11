package com.blog.simpleblog.service;

import com.blog.simpleblog.model.BlogPost;
import com.blog.simpleblog.repository.BlogPostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;

    public BlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    public List<BlogPost> getAllPosts() {
        return blogPostRepository.findAll();
    }

    public BlogPost getPostById(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog post bulunamadı! ID: " + id));
    }

    public BlogPost savePost(BlogPost blogPost) {
        if (blogPost.getTitle() == null || blogPost.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Başlık boş olamaz!");
        }

        if (blogPost.getContent() == null || blogPost.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("İçerik boş olamaz!");
        }

        return blogPostRepository.save(blogPost);
    }

    public BlogPost updatePost(Long id, BlogPost blogPostDetails) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog post bulunamadı! ID: " + id));

        if (blogPostDetails.getTitle() == null || blogPostDetails.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Başlık boş olamaz!");
        }

        if (blogPostDetails.getContent() == null || blogPostDetails.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("İçerik boş olamaz!");
        }

        blogPost.setTitle(blogPostDetails.getTitle());
        blogPost.setContent(blogPostDetails.getContent());
        blogPost.setUpdatedAt(java.time.LocalDateTime.now());

        return blogPostRepository.save(blogPost);
    }

    public void deletePost(Long id) {
        blogPostRepository.deleteById(id);
    }
}
