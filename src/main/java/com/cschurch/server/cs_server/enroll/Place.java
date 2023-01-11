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
public class Place {
    @Id
    private String room; // 방 이름
    private Integer member; // 수용 인원

    @Builder
    public Place(String room, Integer member) {
        this.room = room;
        this.member = member;
    }
}
