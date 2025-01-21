package ru.dovakun.services;

import org.springframework.stereotype.Service;
import ru.dovakun.data.entity.Applicant;
import ru.dovakun.data.entity.TestAssignment;
import ru.dovakun.data.entity.TestResult;
import ru.dovakun.data.entity.User;
import ru.dovakun.repo.TestResultRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestResultService {
    private final TestResultRepo testResultRepo;

    public TestResultService(TestResultRepo testResultRepo) {
        this.testResultRepo = testResultRepo;
    }

    public void save(TestResult testResult) {
        testResultRepo.save(testResult);
    }


    public List<TestResult> findAllByApplicants(List<Applicant> applicants) {
        List<Long> ids = applicants.stream().map(Applicant::getId).toList();
        return testResultRepo.findAllByApplicant(ids);
    }

    public Long calcScore(Long id) {
        return testResultRepo.findAllCalcScore(id);
    }
}
