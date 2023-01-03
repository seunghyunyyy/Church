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
    private String roomName; // 신청한 장소
    private Integer member; // 사용 인원
    private Boolean situation; // 신청을 했으면 true

    @Builder
    public Place_Enroll(String email, String roomName, Integer member, Boolean situation) {
        this.email = email;
        this.roomName = roomName;
        this.member = member;
        this.situation = situation;
    }
}
