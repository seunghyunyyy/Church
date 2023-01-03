package com.cschurch.server.cs_server.enroll;

import com.cschurch.server.cs_server.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EducationEnrollRepository extends JpaRepository<Education_Enroll, Long> {
    Education_Enroll findByUser(User use);
    Education_Enroll findByEducation(Education education);

    List<Education_Enroll> findByUserContaining(User user);
    List<Education_Enroll> findByEducationContaining(Education education);
}
