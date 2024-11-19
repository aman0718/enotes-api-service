package com.codework.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.codework.dto.NotesDto;

public interface NotesService {

    public boolean saveNotes(String notesDto, MultipartFile file) throws Exception;

    public List<NotesDto> getAllNotes();

}
