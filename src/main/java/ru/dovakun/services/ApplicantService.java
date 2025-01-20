package ru.dovakun.services;

import org.springframework.stereotype.Service;
import ru.dovakun.data.entity.Applicant;
import ru.dovakun.repo.ApplicantRepo;

@Service
public class ApplicantService {
    private final ApplicantRepo applicantRepo;

    public ApplicantService(ApplicantRepo applicantRepo) {
        this.applicantRepo = applicantRepo;
    }

    public void save(Applicant applicant) {
        applicantRepo.save(applicant);
    }

    public Applicant getByHash(String s) {
        return applicantRepo.findByHashCode(s);
    }
}
