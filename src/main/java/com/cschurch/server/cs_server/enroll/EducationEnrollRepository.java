package com.cschurch.server.cs_server.enroll;

import com.cschurch.server.cs_server.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EducationEnrollRepository extends JpaRepository<Education_Enroll, Long> {
    Education_Enroll findByEmail(String email);
    Education_Enroll findBySubjectName(String subjectName);

    List<Education_Enroll> findByEmailContaining(String email);
    List<Education_Enroll> findBySubjectNameContaining(String subjectName);
}
