package com.cschurch.server.cs_server.enroll;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@ToString
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Education {
    private String teacher; // 강사
    @Id
    private String subjectName; // 과목명
    private String time; // 시간 : ㅇ요일 00시

    @Builder
    public Education(String teacher, String subjectName, String time) {
        this.teacher = teacher;
        this.subjectName = subjectName;
        this.time = time;
    }
}
