package ru.dovakun.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dovakun.data.entity.TestSession;
import ru.dovakun.repo.TestSessionRepo;

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
}
