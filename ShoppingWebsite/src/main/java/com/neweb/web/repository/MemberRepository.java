package com.neweb.web.repository;

import com.neweb.web.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE Member m SET m.userToken = ?2, m.tokenExpiry = ?3 WHERE m.username = ?1")
    void updateUserToken(String username, String token, Date tokenExpiry);

    @Modifying
    @Query("UPDATE Member m SET m.userToken = null, m.tokenExpiry = null WHERE m.username = ?1")
    void clearUserToken(String username);
}
