package com.codework.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.codework.dto.NotesDto;
import com.codework.entity.FileDetails;
import com.codework.exception.ResourceNotFoundException;

public interface NotesService {

    public boolean saveNotes(String notesDto, MultipartFile file) throws Exception;

    public List<NotesDto> getAllNotes();

    public byte[] downloadFile(FileDetails fileDetails) throws Exception;

    public FileDetails getFileDetails(Integer id) throws ResourceNotFoundException;

}
