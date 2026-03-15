package com.artiselite.warehouse.user.repository;

import com.artiselite.warehouse.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    @EntityGraph(attributePaths = "role")
    Optional<User> findById(Long userId);

    @EntityGraph(attributePaths = "role")
    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndUserIdNot(String email, Long userId);

    @EntityGraph(attributePaths = "role")
    @Query("""
            select u
            from User u
            join u.role r
            where (:roleName is null or upper(r.name) = upper(:roleName))
              and (:isActive is null or u.isActive = :isActive)
            """)
    Page<User> findAllByFilters(
            @Param("roleName") String roleName,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}