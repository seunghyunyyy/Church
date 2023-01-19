package com.cschurch.server.cs_server;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@ToString
@Getter
@Setter
@Entity
@NoArgsConstructor
public class User {
    @Id
    private String email; // 회원 이메일
    private String token; // 기기 토큰
    private String name; // 회원 이름
    private String sex; // 회원 성별
    private Integer age; // 회원 나이
    private String birth; // 회원 생일
    private String phone; // 회원 전화번호
    private String home; // 회원 집 주소
    private String officer; // 회원 직책
    private String profile; // 회원 프로필 사진 주소

    @Builder
    public User(String email, String token, String name, String sex, Integer age,
                String birth, String phone, String home, String officer, String profile) {
        this.email = email;
        this.token = token;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.birth = birth;
        this.phone = phone;
        this.home = home;
        this.officer = officer;
        this.profile = profile;
    }
}
