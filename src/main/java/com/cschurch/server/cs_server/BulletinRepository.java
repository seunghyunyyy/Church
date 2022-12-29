package com.cschurch.server.cs_server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface BulletinRepository extends JpaRepository<Bulletin, String> {
    Optional<Bulletin> findByDate(String date);
}
