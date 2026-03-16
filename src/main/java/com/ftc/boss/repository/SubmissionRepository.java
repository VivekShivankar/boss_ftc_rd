package com.ftc.boss.repository;

import com.ftc.boss.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT s FROM Submission s ORDER BY s.submittedAt DESC")
    List<Submission> findAllOrderByDateDesc();

    List<Submission> findByEmailOrderBySubmittedAtDesc(String email);

    long countByCategory(String category);
    long countByStatus(String status);
}
