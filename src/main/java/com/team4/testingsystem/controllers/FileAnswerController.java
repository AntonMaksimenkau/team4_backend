package com.team4.testingsystem.controllers;

import com.team4.testingsystem.entities.FileAnswer;
import com.team4.testingsystem.services.FileAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/file_answer")
public class FileAnswerController {
    private final FileAnswerService fileAnswerService;

    @Autowired
    public FileAnswerController(FileAnswerService fileAnswerService) {
        this.fileAnswerService = fileAnswerService;
    }

    @GetMapping(path = "/{id}")
    public FileAnswer get(@PathVariable long id) {
        return fileAnswerService.getById(id);
    }

    @PostMapping(path = "/{id}")
    public void create(@PathVariable long id, @RequestBody String url, @RequestBody long questionId) {
        fileAnswerService.create(id, url, questionId);
    }

    @PutMapping(path = "/{id}")
    public void update(@PathVariable long id, String url, long questionId) {
        fileAnswerService.update(id, url, questionId);
    }

    @DeleteMapping(path = "/{id}")
    public void remove(@PathVariable long id) {
        fileAnswerService.removeById(id);
    }
}