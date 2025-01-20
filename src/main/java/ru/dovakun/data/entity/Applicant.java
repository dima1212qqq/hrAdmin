package ru.dovakun.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import ru.dovakun.data.AbstractEntity;

import java.time.OffsetDateTime;


@Data
@Entity
public class Applicant extends AbstractEntity {
    private String name;
    private String resumeLink;
    private String contactMethod;
    private String ipAddress;
    private String hashCode;
    @ManyToOne(fetch = FetchType.LAZY)
    private TestAssignment testAssignment;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

}
