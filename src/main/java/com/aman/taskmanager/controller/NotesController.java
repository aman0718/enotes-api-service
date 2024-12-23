package com.aman.taskmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aman.taskmanager.dto.FavouriteNoteDto;
import com.aman.taskmanager.dto.NotesDto;
import com.aman.taskmanager.dto.NotesResponse;
import com.aman.taskmanager.entity.FileDetails;
import com.aman.taskmanager.service.NotesService;
import com.aman.taskmanager.util.CommonUtil;

@RestController
@RequestMapping("/api/v1/notes")
public class NotesController {

    @Autowired
    private NotesService notesService;

    // --------------------************************************------------------------
    // -------------------------------- Save Notes
    // -----------------------------------

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> saveNotes(@RequestParam String notes, @RequestParam(required = false) MultipartFile file)
            throws Exception {

        Boolean savedNotes = notesService.saveNotes(notes, file);

        if (savedNotes) {
            return CommonUtil.createBuildResponseMessage("Notes saved successfully", HttpStatus.CREATED);
        }
        return CommonUtil.createErrorResponseMessage("Notes not saved", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --------------------************************************------------------------
    // ------------------------------- File Download
    // ----------------------------------

    @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> downloadFile(@PathVariable Integer id) throws Exception {

        FileDetails fileDetails = notesService.getFileDetails(id);

        byte[] downloadFile = notesService.downloadFile(fileDetails);

        HttpHeaders headers = new HttpHeaders();

        String contentType = CommonUtil.getContentType(fileDetails.getOriginalFileName());
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", fileDetails.getOriginalFileName());

        return ResponseEntity.ok().headers(headers).body(downloadFile);
    }

    // --------------------************************************------------------------
    // -------------------------------- Fetch
    // Notes----------------------------------

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> getAllNotes() {
        List<NotesDto> notes = notesService.getAllNotes();
        if (CollectionUtils.isEmpty(notes)) {
            return ResponseEntity.noContent().build();

        }
        return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
    }

    @GetMapping("/user-notes")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getAllNotesByUser(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        Integer userId = 2;
        NotesResponse notes = notesService.getAllNotesByUser(userId, pageNo, pageSize);

        return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
    }

    // --------------------************************************------------------------
    // ------------------------------ Delete Notes
    // -----------------------------------

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> deleteNotes(@PathVariable Integer id) throws Exception {

        notesService.softDeleteNotes(id);
        return CommonUtil.createBuildResponseMessage("Delete success", HttpStatus.OK);
    }

    @GetMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> restoreNotes(@PathVariable Integer id) throws Exception {

        notesService.restoreNotes(id);
        return CommonUtil.createBuildResponseMessage("Restore success", HttpStatus.OK);
    }

    @GetMapping("/recycle-bin")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getUserRecycleBinNotes() throws Exception {

        Integer userId = 2;
        List<NotesDto> notes = notesService.getRecycleBinNotes(userId);
        if (CollectionUtils.isEmpty(notes))
            return CommonUtil.createBuildResponseMessage("Recycle bin is empty!", HttpStatus.NOT_FOUND);
        return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> hardDeleteNotes(@PathVariable Integer id) throws Exception {

        notesService.hardDeleteNotes(id);
        return CommonUtil.createBuildResponseMessage("Successfully deleted from recycle bin", HttpStatus.OK);
    }

    @DeleteMapping("/delete-all")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> emptyRecycleBin() throws Exception {

        int userId = 2;
        notesService.emptyRecycleBin(userId);
        return CommonUtil.createBuildResponseMessage("Notes deleted permanently", HttpStatus.OK);
    }

    // --------------------************************************------------------------
    // -------------------------- Favourite Notes Module
    // ------------------------------

    @GetMapping("/fav/{noteId}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> favouriteNote(@PathVariable Integer noteId) throws Exception {

        notesService.favouriteNotes(noteId);
        return CommonUtil.createBuildResponseMessage("Added to favourites", HttpStatus.CREATED);
    }

    @DeleteMapping("/un-fav/{favNoteId}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> unfavouriteNote(@PathVariable Integer favNoteId) throws Exception {

        notesService.unFavouriteNotes(favNoteId);
        return CommonUtil.createBuildResponseMessage("Removed from favourites", HttpStatus.OK);
    }

    @GetMapping("/favourites")
    @PreAuthorize("hasAnyRole('USER')")

    public ResponseEntity<?> getUserFavouriteNotes() throws Exception {

        List<FavouriteNoteDto> userFavouriteNoteDtos = notesService.getUserFavouriteNotes();

        if (CollectionUtils.isEmpty(userFavouriteNoteDtos)) {
            return ResponseEntity.noContent().build();
        }
        return CommonUtil.createBuildResponse(userFavouriteNoteDtos, HttpStatus.OK);
    }

    @GetMapping("/copy/{id}")
    @PreAuthorize("hasAnyRole('USER')")

    public ResponseEntity<?> copyNotes(@PathVariable Integer id) throws Exception {

        Boolean copyNotes = notesService.copyNotes(id);

        if (copyNotes)
            return CommonUtil.createBuildResponseMessage("Note copied successfully", HttpStatus.CREATED);

        return CommonUtil.createBuildResponseMessage("Note copy failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
