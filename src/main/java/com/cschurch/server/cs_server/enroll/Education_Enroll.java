package com.cschurch.server.cs_server.enroll;

import com.cschurch.server.cs_server.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Education_Enroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email; // 신청한 사람

    private String subjectName; // 신청한 과목
    private Boolean situation; // 신청을 했으면 true
    @Column(name = "enroll_time", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime enrollTime;

    @Builder
    public Education_Enroll(String email, String subjectName, Boolean situation, LocalDateTime enrollTime) {
        this.email = email;
        this.subjectName = subjectName;
        this.situation = situation;
        this.enrollTime = enrollTime;
    }
}
