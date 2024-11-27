package com.aman.taskmanager.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.aman.taskmanager.dto.FavouriteNoteDto;
import com.aman.taskmanager.dto.NotesDto;
import com.aman.taskmanager.dto.NotesResponse;
import com.aman.taskmanager.entity.FileDetails;
import com.aman.taskmanager.exception.ResourceNotFoundException;

public interface NotesService {

    public boolean saveNotes(String notesDto, MultipartFile file) throws Exception;

    public List<NotesDto> getAllNotes();

    public NotesResponse getAllNotesByUser(Integer userId, Integer pageNo, Integer pageSize);

    public byte[] downloadFile(FileDetails fileDetails) throws Exception;

    public FileDetails getFileDetails(Integer id) throws ResourceNotFoundException;

    public void softDeleteNotes(Integer id) throws Exception;

    public void restoreNotes(Integer id) throws Exception;

    public List<NotesDto> getRecycleBinNotes(Integer userId);

    public void hardDeleteNotes(Integer id) throws Exception;

    public void emptyRecycleBin(int userId);

    public void favouriteNotes(Integer noteId) throws Exception;

    public void unFavouriteNotes(Integer noteId) throws Exception;

    public List<FavouriteNoteDto> getUserFavouriteNotes() throws Exception;

    public Boolean copyNotes(Integer id) throws Exception;
}
