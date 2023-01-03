package com.cschurch.server.cs_server.enroll;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PlaceRepository extends JpaRepository<Place, String> {
    Place findByRoomNumber(Long roomNumber);
    Place findByName(String name);
    Place findByMember(Integer member);

    List<Place> findByRoomNumberContaining(Long roomNumber);
    List<Place> findByNameContaining(String name);
    List<Place> findByMemberContaining(Integer member);
}
