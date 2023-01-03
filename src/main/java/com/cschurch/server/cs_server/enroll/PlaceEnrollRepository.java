package com.cschurch.server.cs_server.enroll;

import com.cschurch.server.cs_server.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceEnrollRepository extends JpaRepository<Place_Enroll, Long> {
    Place_Enroll findByEmail(String email);
    Place_Enroll findByRoomName(String roomName);

    List<Place_Enroll> findByEmailContaining(String email);
    List<Place_Enroll> findByRoomNameContaining(String roomName);
}
