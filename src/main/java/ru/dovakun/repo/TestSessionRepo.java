package ru.dovakun.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.dovakun.data.entity.TestSession;

import java.util.List;

public interface TestSessionRepo extends JpaRepository<TestSession, Long> {
    TestSession findByHashCode(String hashCode);
    @Query("select t from TestSession t where t.applicant.id in(:ids)")
    List<TestSession> findAllByApplicants(List<Long> ids);
}
