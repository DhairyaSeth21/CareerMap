package com.careermappro.repositories;

import com.careermappro.entities.Session;
import com.careermappro.entities.Session.SessionState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {

    // Find active or proposed session for user and skill
    Optional<Session> findByUserIdAndSkillNode_SkillNodeIdAndSessionStateIn(
        Integer userId,
        Integer skillNodeId,
        List<SessionState> states
    );

    // Find all sessions for a user by state
    List<Session> findByUserIdAndSessionState(Integer userId, SessionState state);

    // Find current proposed session for user (should only be one at a time)
    Optional<Session> findFirstByUserIdAndSessionStateOrderByCreatedAtDesc(
        Integer userId,
        SessionState state
    );

    // Get all sessions for a user (for history)
    List<Session> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // Find expired sessions
    List<Session> findBySessionStateAndExpiresAtBefore(
        SessionState state,
        java.time.LocalDateTime dateTime
    );
}
