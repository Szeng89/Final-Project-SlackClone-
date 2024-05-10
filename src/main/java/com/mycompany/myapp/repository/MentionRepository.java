package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Mention;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Mention entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MentionRepository extends JpaRepository<Mention, Long> {}
