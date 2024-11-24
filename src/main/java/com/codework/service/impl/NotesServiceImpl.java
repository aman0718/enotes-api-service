package com.codework.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import com.codework.dto.NotesDto;
import com.codework.dto.NotesDto.CategoryDto;
import com.codework.entity.FileDetails;
import com.codework.entity.Notes;
import com.codework.exception.ResourceNotFoundException;
import com.codework.repository.CategoryRepository;
import com.codework.repository.FileRepository;
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
    private FileRepository fileRepository;

    @Autowired
    private ModelMapper mapper;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public boolean saveNotes(String notes, MultipartFile file) throws Exception {

        // Receiving notesDto as string and then converting string to DTO
        ObjectMapper object = new ObjectMapper();
        NotesDto notesDto = object.readValue(notes, NotesDto.class);

        // Category Validation
        ValidateCategory(notesDto.getCategory());

        // DTO to Entity
        Notes notesEntity = mapper.map(notesDto, Notes.class);

        // Storing files locally on system
        FileDetails fileDetails = savFileDetails(file);

        if (!ObjectUtils.isEmpty(fileDetails)) {
            notesEntity.setFileDetails(fileDetails);
        } else {
            notesEntity.setFileDetails(null);
        }

        Notes savedNotes = notesRepository.save(notesEntity);

        if (!ObjectUtils.isEmpty(savedNotes)) {
            return true;
        }
        return false;
    }

    private FileDetails savFileDetails(MultipartFile file) throws IOException {

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
                .orElseThrow(() -> new ResourceNotFoundException("Category ID is invalid"));
    }

    @Override
    public List<NotesDto> getAllNotes() {

        return notesRepository.findAll().stream()
                .map(note -> mapper.map(note, NotesDto.class)).toList();
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

}
