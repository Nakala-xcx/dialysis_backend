package com.histsys.data.repository;

import com.histsys.data.model.User;
import com.histsys.data.querys.Searchable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, Searchable<User> {
    Page<User> findAll(Pageable pageable);
    Optional<User> findById(Long id); // staffId
    Optional<User> findByStaffNo(String staffNo);
    void deleteAllByIdIn(List<Long> ids);
}
