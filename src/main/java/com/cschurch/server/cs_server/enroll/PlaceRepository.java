package com.cschurch.server.cs_server.enroll;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PlaceRepository extends JpaRepository<Place, String> {
    Place findByRoom(String room);
    Place findByMember(Integer member);

    List<Place> findByRoomContaining(String room);
    List<Place> findByMemberContaining(Integer member);
}
