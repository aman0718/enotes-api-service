package com.codework.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.codework.entity.Notes;

public interface NotesRepository extends JpaRepository<Notes, Integer> {

    Page<Notes> findByCreatedBy(Integer userId, Pageable pageable);

}
