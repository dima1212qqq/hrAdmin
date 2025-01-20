package ru.dovakun.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dovakun.data.entity.TestResult;

public interface TestResultRepo extends JpaRepository<TestResult, Long> {
}
