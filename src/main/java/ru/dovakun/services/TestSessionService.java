package ru.dovakun.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dovakun.data.entity.Applicant;
import ru.dovakun.data.entity.TestSession;
import ru.dovakun.repo.TestSessionRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestSessionService {
    private final TestSessionRepo testSessionRepo;

    public TestSessionService(TestSessionRepo testSessionRepo) {
        this.testSessionRepo = testSessionRepo;
    }
    @Transactional
    public void save(TestSession session) {
        testSessionRepo.save(session);
    }
    @Transactional
    public TestSession getByHash(String hash) {
        return testSessionRepo.findByHashCode(hash);
    }

    public List<TestSession> findAllByApplicants(List<Applicant> applicants) {
        List<Long> ids = applicants.stream().map(Applicant::getId).toList();
        return testSessionRepo.findAllByApplicants(ids);
    }
}
