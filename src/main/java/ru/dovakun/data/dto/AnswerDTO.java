package ru.dovakun.data.dto;

import lombok.Data;

@Data
public class AnswerDTO {
    private String title;
    private double score;
    private boolean requires;
    private String detailsHint;
}
