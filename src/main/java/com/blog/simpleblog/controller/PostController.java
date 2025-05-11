package com.blog.simpleblog.controller;

import com.blog.simpleblog.model.BlogPost;
import com.blog.simpleblog.service.BlogPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/blog")
public class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @GetMapping("/posts")
    public List<BlogPost> getAllPosts() {
        return blogPostService.getAllPosts();
    }

    @GetMapping("/posts/{id}")
    public BlogPost getPostById(@PathVariable Long id) {
        return blogPostService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Blog bulunamadı! ID: " + id));
    }

    @PostMapping("/posts")
    public ResponseEntity<BlogPost> createPost(@RequestBody BlogPost blogPost) {
        BlogPost savedPost = blogPostService.savePost(blogPost);
        return ResponseEntity.ok(savedPost);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<BlogPost> updatePost(@PathVariable Long id, @RequestBody BlogPost blogPost) {
        BlogPost updatedPost = blogPostService.updatePost(id, blogPost);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        blogPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
