package com.cschurch.server.cs_server.enroll;

import com.cschurch.server.cs_server.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EducationEnrollRepository extends JpaRepository<Education_Enroll, Long> {
    Education_Enroll findByEmail(String email);
    Education_Enroll findBySubject(String subject);

    List<Education_Enroll> findByEmailContaining(String email, Sort sort);
    List<Education_Enroll> findBySubjectContaining(String subject, Sort sort);

}
