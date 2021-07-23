package com.team4.testingsystem.services.impl;

import com.team4.testingsystem.dto.FileAnswerRequest;
import com.team4.testingsystem.entities.FileAnswer;
import com.team4.testingsystem.exceptions.FileNotFoundException;
import com.team4.testingsystem.exceptions.NotFoundException;
import com.team4.testingsystem.exceptions.QuestionNotFoundException;
import com.team4.testingsystem.repositories.FileAnswerRepository;
import com.team4.testingsystem.repositories.QuestionRepository;
import com.team4.testingsystem.services.FileAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileAnswerServiceImpl implements FileAnswerService {
    private final FileAnswerRepository fileAnswerRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public FileAnswerServiceImpl(FileAnswerRepository fileAnswerRepository, QuestionRepository questionRepository) {
        this.fileAnswerRepository = fileAnswerRepository;
        this.questionRepository = questionRepository;
    }

    public void create(FileAnswerRequest fileAnswerRequest) {
        FileAnswer fileAnswer = FileAnswer.builder()
                .url(fileAnswerRequest.getUrl())
                .question(questionRepository.findById(fileAnswerRequest.getQuestionId())
                        .orElseThrow(QuestionNotFoundException::new))
                .build();
        fileAnswerRepository.save(fileAnswer);
    }

    public FileAnswer getById(long id) {
        return fileAnswerRepository.findById(id)
                .orElseThrow(FileNotFoundException::new);
    }

    public void update(long id, FileAnswerRequest fileAnswerRequest) {
        FileAnswer fileAnswer = FileAnswer.builder()
                .id(fileAnswerRepository.findById(id)
                        .orElseThrow(FileNotFoundException::new)
                        .getId())
                .url(fileAnswerRequest.getUrl())
                .question(questionRepository.findById(fileAnswerRequest.getQuestionId())
                        .orElseThrow(QuestionNotFoundException::new))
                .build();
        fileAnswerRepository.save(fileAnswer);
    }

    public void removeById(long id) {
        if (!fileAnswerRepository.existsById(id)) {
            throw new FileNotFoundException();
        }
        fileAnswerRepository.deleteById(id);
    }
}
