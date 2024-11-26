package com.codework.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.codework.entity.Notes;
import com.codework.repository.NotesRepository;

@Component
public class NotesScheduler {

    @Autowired
    private NotesRepository notesRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteNotesScheduler() {

        // 20-14 Nov => 7 days
        LocalDateTime cutOfDate = LocalDateTime.now().minusDays(7);
        List<Notes> deletedNotes = notesRepository.findAllByIsDeletedAndDeletedOnBefore(true, cutOfDate);
        notesRepository.deleteAll(deletedNotes);
    }
}
