package com.example.demo.repository;

import com.example.demo.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 1. 기본 검색 기능
    List<Post> findByTitleContaining(String title);
    List<Post> findByContentContaining(String content);
    List<Post> findByAuthor(String author);
    
    // 2. 제목과 내용에서 동시 검색
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    List<Post> findByTitleOrContentContaining(@Param("keyword") String keyword);
    
    // 3. 삭제되지 않은 게시글만 조회
    List<Post> findByIsDeletedFalse();
    Page<Post> findByIsDeletedFalse(Pageable pageable);
    
    // 4. 조회수 관련
    List<Post> findByViewCountGreaterThan(Integer viewCount);
    List<Post> findByViewCountGreaterThanOrderByViewCountDesc(Integer viewCount);
    
    // 5. 날짜 범위 검색
    List<Post> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Post> findByCreatedAtAfter(LocalDateTime date);
    
    // 6. 최근 게시글 조회
    List<Post> findTop10ByOrderByCreatedAtDesc();
    List<Post> findTop5ByOrderByViewCountDesc();
    
    // 7. 특정 작성자의 최근 게시글
    List<Post> findTop5ByAuthorOrderByCreatedAtDesc(String author);
    
    // 8. 조회수 증가 메서드
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);
    
    // 9. 소프트 삭제
    @Modifying
    @Query("UPDATE Post p SET p.isDeleted = true WHERE p.id = :postId")
    void softDelete(@Param("postId") Long postId);
    
    // 10. 통계 쿼리
    @Query("SELECT COUNT(p) FROM Post p WHERE p.isDeleted = false")
    Long countActivePosts();
    
    @Query("SELECT p.author, COUNT(p) FROM Post p WHERE p.isDeleted = false GROUP BY p.author ORDER BY COUNT(p) DESC")
    List<Object[]> countPostsByAuthor();
    
    // 11. 복합 검색 (제목 + 작성자 + 날짜 범위)
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false " +
           "AND (:title IS NULL OR p.title LIKE %:title%) " +
           "AND (:author IS NULL OR p.author = :author) " +
           "AND (:startDate IS NULL OR p.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR p.createdAt <= :endDate) " +
           "ORDER BY p.createdAt DESC")
    Page<Post> searchPosts(
        @Param("title") String title,
        @Param("author") String author,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    // 12. 인기 게시글 (조회수 기준)
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND p.viewCount >= :minViewCount ORDER BY p.viewCount DESC")
    Page<Post> findPopularPosts(@Param("minViewCount") Integer minViewCount, Pageable pageable);
}
