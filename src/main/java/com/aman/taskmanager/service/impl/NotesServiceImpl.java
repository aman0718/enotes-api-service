package com.aman.taskmanager.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import com.aman.taskmanager.dto.FavouriteNoteDto;
import com.aman.taskmanager.dto.NotesDto;
import com.aman.taskmanager.dto.NotesResponse;
import com.aman.taskmanager.dto.NotesDto.CategoryDto;
import com.aman.taskmanager.dto.NotesDto.FileDto;
import com.aman.taskmanager.entity.FavouriteNote;
import com.aman.taskmanager.entity.FileDetails;
import com.aman.taskmanager.entity.Notes;
import com.aman.taskmanager.exception.ResourceNotFoundException;
import com.aman.taskmanager.repository.CategoryRepository;
import com.aman.taskmanager.repository.FavouriteNoteRepository;
import com.aman.taskmanager.repository.FileRepository;
import com.aman.taskmanager.repository.NotesRepository;
import com.aman.taskmanager.service.NotesService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NotesServiceImpl implements NotesService {

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FavouriteNoteRepository favouriteNoteRepository;

    @Autowired
    private ModelMapper mapper;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public boolean saveNotes(String notes, MultipartFile file) throws Exception {

        // Receiving notesDto as string and then converting string to DTO
        ObjectMapper object = new ObjectMapper();
        NotesDto notesDto = object.readValue(notes, NotesDto.class);

        notesDto.setIsDeleted(false);
        notesDto.setDeletedOn(null);

        // Scope of improvement for better implementation //

        // Checking if ID exists or not
        if (!ObjectUtils.isEmpty(notesDto.getId())) {
            updateNotes(notesDto, file);
        }

        // Category Validation
        ValidateCategory(notesDto.getCategory());

        // DTO to Entity
        Notes notesEntity = mapper.map(notesDto, Notes.class);

        // Validating & Storing files locally on system
        FileDetails fileDetails = handleFileUpload(file);

        if (!ObjectUtils.isEmpty(fileDetails)) {
            notesEntity.setFileDetails(fileDetails);
        } else {
            if (ObjectUtils.isEmpty(notesDto.getId()))
                notesEntity.setFileDetails(null);
        }

        Notes savedNotes = notesRepository.save(notesEntity);
        if (!ObjectUtils.isEmpty(savedNotes)) {
            return true;
        }
        return false;
    }

    private void updateNotes(NotesDto notesDto, MultipartFile file) throws Exception {

        Notes existingNote = notesRepository.findById(notesDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Id"));

        existingNote.setIsDeleted(false);
        existingNote.setDeletedOn(null);
        if (ObjectUtils.isEmpty(file)) {
            notesDto.setFileDetails(mapper.map(existingNote.getFileDetails(), FileDto.class));
        }
    }

    private FileDetails handleFileUpload(MultipartFile file) throws IOException {

        if (!ObjectUtils.isEmpty(file) && !file.isEmpty()) {

            // Seperating file_name and extension_name from file.
            String originalFileName = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFileName);

            // Validating below mentioned file types allowed to upload
            List<String> allowedExtensions = Arrays.asList("pdf", "xlsx", "jpg", "png", "webp");
            if (!allowedExtensions.contains(extension)) {
                throw new IllegalArgumentException("Only " + allowedExtensions + " files are allowed.");
            }

            // Validating upload path
            File saveFile = new File(uploadPath);
            if (!saveFile.exists()) {
                saveFile.mkdir();
            }

            // Setting up file name which will be stored on DB
            String randomString = UUID.randomUUID().toString();
            String uploadFileName = randomString + "." + extension;

            // Final path - enotes-api-sevice/notes/file_name.extension
            String storePath = uploadPath.concat(uploadFileName);

            // Copying files to directory & saving file_details on DB.
            long upload = Files.copy(file.getInputStream(), Paths.get(storePath));
            if (upload != 0) {

                FileDetails fileDetails = new FileDetails();

                fileDetails.setOriginalFileName(originalFileName);
                fileDetails.setDisplayFileName(getDisplayFileName(originalFileName));
                fileDetails.setUploadFileName(uploadFileName);
                fileDetails.setFileSize(file.getSize());
                fileDetails.setPath(storePath);
                FileDetails saveFileDetails = fileRepository.save(fileDetails);
                return saveFileDetails;
            }
        }
        return null;
    }

    private String getDisplayFileName(String originalFileName) {

        String extension = FilenameUtils.getExtension(originalFileName);
        String fileName = FilenameUtils.removeExtension(originalFileName);

        if (fileName.length() > 8) {
            fileName = fileName.substring(0, 7);
        }
        fileName = fileName + "." + extension;
        return fileName;
    }

    private void ValidateCategory(CategoryDto category) throws Exception {
        categoryRepository.findById(category.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category ID " + category.getId() + " is invalid"));
    }

    @Override
    public byte[] downloadFile(FileDetails fileDetails) throws Exception {

        InputStream io = new FileInputStream(fileDetails.getPath());
        return StreamUtils.copyToByteArray(io);
    }

    @Override
    public FileDetails getFileDetails(Integer id) throws ResourceNotFoundException {
        FileDetails fileDetails = fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("file not available"));
        return fileDetails;
    }

    @Override
    public List<NotesDto> getAllNotes() {
        return notesRepository.findAll().stream()
                .map(note -> mapper.map(note, NotesDto.class)).toList();
    }

    @Override
    public NotesResponse getAllNotesByUser(Integer userId, Integer pageNo, Integer pageSize) {

        // Say-> Total 10 elements. each page should show 5 elements, means 2 pages will
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Notes> notes = notesRepository.findByCreatedByAndIsDeletedFalse(userId, pageable);

        // Converting Entity to DTO
        List<NotesDto> notesDto = notes.get().map(n -> mapper.map(n, NotesDto.class)).toList();

        // Setting up Notes-Response-DTO
        NotesResponse notesResponse = NotesResponse.builder()
                .notes(notesDto)
                .pageNumber(notes.getNumber())
                .pageSize(notes.getSize())
                .totalElements(notes.getTotalElements())
                .totalPages(notes.getTotalPages())
                .isFirst(notes.isFirst())
                .isLast(notes.isLast())
                .build();

        return notesResponse;
    }

    @Override
    public void softDeleteNotes(Integer id) throws Exception {

        Notes notes = notesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note id is invalid"));

        notes.setIsDeleted(true);
        notes.setDeletedOn(LocalDateTime.now());
        notesRepository.save(notes);
    }

    @Override
    public void restoreNotes(Integer id) throws Exception {
        Notes notes = notesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note id is invalid"));

        notes.setIsDeleted(false);
        notes.setDeletedOn(null);
        notesRepository.save(notes);
    }

    @Override
    public List<NotesDto> getRecycleBinNotes(Integer userId) {
        List<Notes> recycleBinNotes = notesRepository.findByCreatedByAndIsDeletedTrue(userId);
        List<NotesDto> notesList = recycleBinNotes.stream().map(note -> mapper.map(note, NotesDto.class)).toList();
        return notesList;
    }

    @Override
    public void hardDeleteNotes(Integer id) throws Exception {

        Notes notes = notesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notes not found"));

        if (notes.getIsDeleted()) {
            notesRepository.delete(notes);
        } else {
            throw new IllegalArgumentException("You cannot delete this note");
        }
    }

    @Override
    public void emptyRecycleBin(int userId) {
        List<Notes> recycleBinNotes = notesRepository.findByCreatedByAndIsDeletedTrue(userId);

        if (!CollectionUtils.isEmpty(recycleBinNotes)) {
            notesRepository.deleteAll(recycleBinNotes);
        }
    }

    @Override
    public void favouriteNotes(Integer noteId) throws Exception {

        int usedId = 2;

        Notes note = notesRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));

        FavouriteNote favouriteNote = FavouriteNote.builder()
                .note(note)
                .userId(usedId)
                .build();

        favouriteNoteRepository.save(favouriteNote);
    }

    @Override
    public void unFavouriteNotes(Integer favouriteNoteId) throws Exception {

        FavouriteNote favNote = favouriteNoteRepository.findById(favouriteNoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid !. This note is not added as favourite"));

        favouriteNoteRepository.delete(favNote);
    }

    @Override
    public List<FavouriteNoteDto> getUserFavouriteNotes() throws Exception {

        int userId = 2;

        List<FavouriteNote> favouriteNotes = favouriteNoteRepository.findByUserId(userId);
        return favouriteNotes.stream().map(fn -> mapper.map(fn, FavouriteNoteDto.class)).toList();
    }

    @Override
    public Boolean copyNotes(Integer id) throws Exception {

        Notes notes = notesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Note not found"));

        // Notes copyNote = new Notes();
        // copyNote.setTitle(notes.getTitle());

        Notes copyNote = Notes.builder()
                .title(notes.getTitle())
                .description(notes.getDescription())
                .category(notes.getCategory())
                .isDeleted(false)
                .deletedOn(null)
                .fileDetails(null)
                .build();

                
        // TODO: Need to check User Validation
        Notes saveNote = notesRepository.save(copyNote);
        if (!ObjectUtils.isEmpty(saveNote))
            return true;

        return false;
    }
}
