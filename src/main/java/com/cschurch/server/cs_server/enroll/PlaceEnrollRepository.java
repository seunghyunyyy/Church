package com.cschurch.server.cs_server.enroll;

import com.cschurch.server.cs_server.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceEnrollRepository extends JpaRepository<Place_Enroll, Long> {
    Place_Enroll findByUser(User user);
    Place_Enroll findByPlace(Place place);

    List<Place_Enroll> findByUserContaining(User user);
    List<Place_Enroll> findByPlaceContaining(Place place);
}
