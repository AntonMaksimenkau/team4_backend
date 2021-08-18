package com.team4.testingsystem.services;

import com.team4.testingsystem.entities.Answer;
import org.springframework.web.multipart.MultipartFile;

public interface AnswerService {
    Answer getById(Long answerId);

    String downloadEssay(Long testId);

    String uploadEssay(Long testId, String text);

    String downloadSpeaking(Long testId);

    String uploadSpeaking(MultipartFile file, Long testId);

}
