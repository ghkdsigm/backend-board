package com.example.demo.controller;

import com.example.demo.entity.Post;
import com.example.demo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 1. 게시글 생성
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return ResponseEntity.ok(createdPost);
    }

    // 2. 게시글 조회 (조회수 증가)
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Optional<Post> post = postService.getPostWithViewCount(id);
        return post.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post updatePost) {
        Optional<Post> post = postService.updatePost(id, updatePost);
        return post.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        boolean deleted = postService.deletePost(id);
        if (deleted) {
            return ResponseEntity.ok("게시글이 삭제되었습니다.");
        }
        return ResponseEntity.notFound().build();
    }

    // 5. 전체 게시글 조회 (페이징)
    @GetMapping
    public ResponseEntity<Page<Post>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Post> posts = postService.getAllPosts(page, size);
        return ResponseEntity.ok(posts);
    }

    // 6. 키워드 검색
    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam String keyword) {
        List<Post> posts = postService.searchPosts(keyword);
        return ResponseEntity.ok(posts);
    }

    // 7. 작성자별 게시글 조회
    @GetMapping("/author/{author}")
    public ResponseEntity<List<Post>> getPostsByAuthor(@PathVariable String author) {
        List<Post> posts = postService.getPostsByAuthor(author);
        return ResponseEntity.ok(posts);
    }

    // 8. 최근 게시글 조회
    @GetMapping("/recent")
    public ResponseEntity<List<Post>> getRecentPosts() {
        List<Post> posts = postService.getRecentPosts();
        return ResponseEntity.ok(posts);
    }

    // 9. 인기 게시글 조회
    @GetMapping("/popular")
    public ResponseEntity<List<Post>> getPopularPosts() {
        List<Post> posts = postService.getPopularPosts();
        return ResponseEntity.ok(posts);
    }

    // 10. 복합 검색
    @GetMapping("/search/advanced")
    public ResponseEntity<Page<Post>> searchPostsAdvanced(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Post> posts = postService.searchPostsAdvanced(title, author, startDate, endDate, page, size);
        return ResponseEntity.ok(posts);
    }

    // 11. 인기 게시글 (조회수 기준)
    @GetMapping("/popular/view-count")
    public ResponseEntity<Page<Post>> getPopularPostsByViewCount(
            @RequestParam(defaultValue = "10") int minViewCount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Post> posts = postService.getPopularPostsByViewCount(minViewCount, page, size);
        return ResponseEntity.ok(posts);
    }

    // 12. 통계 정보
    @GetMapping("/stats/count")
    public ResponseEntity<Long> getActivePostCount() {
        Long count = postService.getActivePostCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/by-author")
    public ResponseEntity<List<Object[]>> getPostCountByAuthor() {
        List<Object[]> stats = postService.getPostCountByAuthor();
        return ResponseEntity.ok(stats);
    }

    // 13. 날짜 범위 검색
    @GetMapping("/date-range")
    public ResponseEntity<List<Post>> getPostsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<Post> posts = postService.getPostsByDateRange(startDate, endDate);
        return ResponseEntity.ok(posts);
    }

    // 14. 조회수가 높은 게시글
    @GetMapping("/high-view-count")
    public ResponseEntity<List<Post>> getPostsWithHighViewCount(
            @RequestParam(defaultValue = "100") int minViewCount) {
        
        List<Post> posts = postService.getPostsWithHighViewCount(minViewCount);
        return ResponseEntity.ok(posts);
    }

    // 15. 특정 작성자의 최근 게시글
    @GetMapping("/author/{author}/recent")
    public ResponseEntity<List<Post>> getRecentPostsByAuthor(@PathVariable String author) {
        List<Post> posts = postService.getRecentPostsByAuthor(author);
        return ResponseEntity.ok(posts);
    }
}
