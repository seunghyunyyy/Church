package com.cschurch.server.cs_server.enroll;

import com.cschurch.server.cs_server.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, String>{
    Education findByTeacher(String teacher);
    Education findBySubjectName(String subjectName);
    Education findByTime(String time);

    List<Education> findByTeacherContaining(String teacher);
    List<Education> findBySubjectNameContaining(String subjectName);
    List<Education> findByTimeContaining(String time);
}
