package ru.dovakun.services;

import org.springframework.stereotype.Service;
import ru.dovakun.data.entity.TestResult;
import ru.dovakun.repo.TestResultRepo;

@Service
public class TestResultService {
    private final TestResultRepo testResultRepo;

    public TestResultService(TestResultRepo testResultRepo) {
        this.testResultRepo = testResultRepo;
    }

    public void save(TestResult testResult) {
        testResultRepo.save(testResult);
    }
}
