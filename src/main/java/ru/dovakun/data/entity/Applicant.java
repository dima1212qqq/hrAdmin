package ru.dovakun.data.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.dovakun.data.AbstractEntity;
import ru.dovakun.data.enums.Status;

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
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

}
