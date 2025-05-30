package com.focussu.backend.analysis;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@Table(name = "study_focus_analysis")
public class StudyFocusAnalysis {
}
