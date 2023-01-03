package com.cschurch.server.cs_server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
    User findByToken(String token);
    User findByName(String name);
    User findBySex(String sex);
    User findByAge(Integer age);
    User findByBirth(String birth);
    User findByPhone(String phone);
    User findByHome(String home);
    User findByOfficer(String officer);
    User findByProfile(String profile);

    List<User> findByEmailContaining(String email);
    List<User> findByTokenContaining(String token);
    List<User> findByNameContaining(String name);
    List<User> findBySexContaining(String sex);
    List<User> findByAgeContaining(Integer age);
    List<User> findByBirthContaining(String birth);
    List<User> findByPhoneContaining(String phone);
    List<User> findByHomeContaining(String home);
    List<User> findByOfficerContaining(String officer);
    List<User> findByProfileContaining(String profile);
}
