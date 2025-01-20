package ru.dovakun.services;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import ru.dovakun.data.entity.TestAssignment;
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

    public TestAssignment getByLink(String s){
        return testAssignmentRepo.findByUniqueLink(s);
    }
}
