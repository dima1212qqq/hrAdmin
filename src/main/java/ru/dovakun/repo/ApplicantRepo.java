package ru.dovakun.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dovakun.data.entity.Applicant;

import java.util.List;

public interface ApplicantRepo extends JpaRepository<Applicant, Long> {
    Applicant findByHashCode(String hashCode);
}
