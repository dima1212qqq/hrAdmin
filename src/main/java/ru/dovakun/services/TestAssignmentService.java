package ru.dovakun.services;

import org.springframework.stereotype.Service;
import ru.dovakun.data.entity.TestAssignment;
import ru.dovakun.data.entity.User;
import ru.dovakun.repo.TestAssignmentRepo;

import java.util.List;

@Service
public class TestAssignmentService {

    private final TestAssignmentRepo testAssignmentRepo;

    public TestAssignmentService(TestAssignmentRepo testAssignmentRepo) {
        this.testAssignmentRepo = testAssignmentRepo;
    }

    public List<TestAssignment> getTestAssignmentsByUser(Long userId) {
        return testAssignmentRepo.findAllByUser(userId);
    }
    public void save(TestAssignment testAssignment) {
        testAssignmentRepo.save(testAssignment);
    }
}
