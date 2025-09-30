package com.jm.futelove.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Candidate {
    private Content content;
    private String finishReason;
    private Integer index;
    private List<SafetyRating> safetyRatings;
}
