package com.codework.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codework.entity.Notes;

public interface NotesRepository extends JpaRepository<Notes, Integer> {

}
