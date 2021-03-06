package com.team4.testingsystem.controllers;

import com.team4.testingsystem.services.AnswerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/answer")
public class AnswerController {

    private final AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @ApiOperation(value = "Upload essay text")
    @PostMapping("/essay/{testId}")
    public String uploadEssay(@PathVariable Long testId, @RequestBody String text) {
        return answerService.uploadEssay(testId, text);
    }

    @ApiOperation("Upload an answer file for the speaking module")
    @PostMapping("/speaking/{testId}")
    public String uploadSpeaking(@RequestPart MultipartFile file,
                                 @PathVariable("testId") Long testId) {
        return answerService.uploadSpeaking(file, testId);
    }
}
