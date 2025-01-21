package ru.dovakun.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dovakun.data.AbstractEntity;
import ru.dovakun.data.entity.Applicant;
import ru.dovakun.data.entity.TestAssignment;
import ru.dovakun.repo.ApplicantRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicantService {
    private final ApplicantRepo applicantRepo;

    public ApplicantService(ApplicantRepo applicantRepo) {
        this.applicantRepo = applicantRepo;
    }
    @Transactional
    public void save(Applicant applicant) {
        applicantRepo.save(applicant);
    }

    public Applicant getByHash(String s) {
        return applicantRepo.findByHashCode(s);
    }

    public List<Applicant> findAllByTest(List<TestAssignment> testAssignments) {
        List<Long> ids = testAssignments.stream().map(TestAssignment::getId).toList();
        return applicantRepo.findAllByTestAssignment_Id(ids);
    }
    public List<Applicant> findAllByTest(TestAssignment testAssignments) {
        return applicantRepo.findAllByTestAssignment(testAssignments.getId());
    }
}
