package com.codework.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.codework.dto.NotesDto;
import com.codework.dto.NotesDto.CategoryDto;
import com.codework.entity.Notes;
import com.codework.exception.ResourceNotFoundException;
import com.codework.repository.CategoryRepository;
import com.codework.repository.NotesRepository;
import com.codework.service.NotesService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NotesServiceImpl implements NotesService {

    @Autowired
    private NotesRepository notesRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper mapper;

    @Override
    public boolean saveNotes(String notes, MultipartFile file) throws Exception {

        // Receiving notesDto as string and then converting string to DTO
        ObjectMapper object = new ObjectMapper();
        NotesDto notesDto = object.readValue(notes, NotesDto.class);

        // Validation
        ValidateCategory(notesDto.getCategory());

        // DTO to Entity
        Notes notesEntity = mapper.map(notesDto, Notes.class);
        Notes savedNotes = notesRepository.save(notesEntity);

        if (!ObjectUtils.isEmpty(savedNotes)) {
            return true;
        }
        return false;
    }

    private void ValidateCategory(CategoryDto category) throws Exception {
        categoryRepository.findById(category.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category ID is invalid"));
    }

    @Override
    public List<NotesDto> getAllNotes() {

        return notesRepository.findAll().stream()
                .map(note -> mapper.map(note, NotesDto.class)).toList();
    }

}
