
package org.example.backend.mentor.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.dto.MentorDto;
import org.example.backend.mentor.service.MentorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mentors")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    @GetMapping
    public ResponseEntity<List<MentorDto>> getAllMentors() {
        return ResponseEntity.ok(mentorService.getAllMentors());
    }
}
