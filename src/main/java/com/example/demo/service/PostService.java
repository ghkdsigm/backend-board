package com.example.demo.service;

import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    // 1. 게시글 생성
    @Transactional
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    // 2. 게시글 조회 (조회수 증가)
    @Transactional
    public Optional<Post> getPostWithViewCount(Long id) {
        Optional<Post> post = postRepository.findById(id);
        post.ifPresent(p -> {
            if (!p.getIsDeleted()) {
                postRepository.incrementViewCount(id);
                p.setViewCount(p.getViewCount() + 1);
            }
        });
        return post;
    }

    // 3. 게시글 수정
    @Transactional
    public Optional<Post> updatePost(Long id, Post updatePost) {
        return postRepository.findById(id)
                .map(post -> {
                    if (!post.getIsDeleted()) {
                        post.setTitle(updatePost.getTitle());
                        post.setContent(updatePost.getContent());
                        return postRepository.save(post);
                    }
                    return null;
                });
    }

    // 4. 게시글 삭제 (소프트 삭제)
    @Transactional
    public boolean deletePost(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent() && !post.get().getIsDeleted()) {
            postRepository.softDelete(id);
            return true;
        }
        return false;
    }

    // 5. 전체 게시글 조회 (페이징)
    public Page<Post> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByIsDeletedFalse(pageable);
    }

    // 6. 키워드 검색
    public List<Post> searchPosts(String keyword) {
        return postRepository.findByTitleOrContentContaining(keyword);
    }

    // 7. 작성자별 게시글 조회
    public List<Post> getPostsByAuthor(String author) {
        return postRepository.findByAuthor(author);
    }

    // 8. 최근 게시글 조회
    public List<Post> getRecentPosts() {
        return postRepository.findTop10ByOrderByCreatedAtDesc();
    }

    // 9. 인기 게시글 조회
    public List<Post> getPopularPosts() {
        return postRepository.findTop5ByOrderByViewCountDesc();
    }

    // 10. 복합 검색
    public Page<Post> searchPostsAdvanced(String title, String author, 
                                        LocalDateTime startDate, LocalDateTime endDate, 
                                        int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.searchPosts(title, author, startDate, endDate, pageable);
    }

    // 11. 인기 게시글 (조회수 기준)
    public Page<Post> getPopularPostsByViewCount(int minViewCount, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findPopularPosts(minViewCount, pageable);
    }

    // 12. 통계 정보
    public Long getActivePostCount() {
        return postRepository.countActivePosts();
    }

    public List<Object[]> getPostCountByAuthor() {
        return postRepository.countPostsByAuthor();
    }

    // 13. 날짜 범위 검색
    public List<Post> getPostsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return postRepository.findByCreatedAtBetween(startDate, endDate);
    }

    // 14. 조회수가 높은 게시글
    public List<Post> getPostsWithHighViewCount(int minViewCount) {
        return postRepository.findByViewCountGreaterThanOrderByViewCountDesc(minViewCount);
    }

    // 15. 특정 작성자의 최근 게시글
    public List<Post> getRecentPostsByAuthor(String author) {
        return postRepository.findTop5ByAuthorOrderByCreatedAtDesc(author);
    }
}
