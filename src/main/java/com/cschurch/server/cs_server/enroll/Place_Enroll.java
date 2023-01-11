package com.cschurch.server.cs_server.enroll;

import com.cschurch.server.cs_server.User;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Place_Enroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email; // 신청한 사람
    private String room; // 신청한 장소
    private Integer member; // 사용 인원
    private String start; // 사용 시작 시간
    private String end; // 사용 종료 시간
    private Boolean situation; // 신청을 했으면 true
    private String enrollTime;

    @Builder
    public Place_Enroll(String email, String room, Integer member, String start, String end, Boolean situation, String enrollTime) {
        this.email = email;
        this.room = room;
        this.member = member;
        this.start = start;
        this.end = end;
        this.situation = situation;
        this.enrollTime = enrollTime;
    }
}
