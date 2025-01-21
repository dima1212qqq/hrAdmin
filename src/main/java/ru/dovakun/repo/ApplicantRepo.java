package ru.dovakun.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.dovakun.data.entity.Applicant;

import java.util.List;

public interface ApplicantRepo extends JpaRepository<Applicant, Long> {
    Applicant findByHashCode(String hashCode);

    @Query("select a from Applicant a where a.testAssignment.id in (:ids)")
    List<Applicant> findAllByTestAssignment_Id(List<Long> ids);
}
