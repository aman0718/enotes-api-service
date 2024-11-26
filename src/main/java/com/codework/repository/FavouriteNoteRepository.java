package com.codework.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codework.entity.FavouriteNote;

public interface FavouriteNoteRepository extends JpaRepository<FavouriteNote, Integer> {

    List<FavouriteNote> findByUserId(int userId);

}