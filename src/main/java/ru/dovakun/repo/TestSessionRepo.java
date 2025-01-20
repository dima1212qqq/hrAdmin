package ru.dovakun.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dovakun.data.entity.TestSession;

public interface TestSessionRepo extends JpaRepository<TestSession, Long> {
    TestSession findByHashCode(String hashCode);
}
