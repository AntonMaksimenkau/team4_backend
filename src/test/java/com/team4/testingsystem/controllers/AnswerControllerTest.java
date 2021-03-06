package com.team4.testingsystem.controllers;

import com.team4.testingsystem.exceptions.FileSavingFailedException;
import com.team4.testingsystem.exceptions.QuestionNotFoundException;
import com.team4.testingsystem.exceptions.TestNotFoundException;
import com.team4.testingsystem.services.AnswerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AnswerControllerTest {

    @Mock
    private AnswerService answerService;

    @Mock
    private MultipartFile file;


    @InjectMocks
    private AnswerController answerController;

    private static final Long TEST_ID = 1L;
    private static final String ESSAY_TEXT = "text";
    private static final String URL = "some url";

    @Test
    void uploadEssayTestNotFound() {
        Mockito.doThrow(TestNotFoundException.class)
                .when(answerService).uploadEssay(TEST_ID, ESSAY_TEXT);

        Assertions.assertThrows(TestNotFoundException.class,
                () -> answerController.uploadEssay(TEST_ID, ESSAY_TEXT));
    }

    @Test
    void uploadEssayQuestionNotFound() {
        Mockito.doThrow(QuestionNotFoundException.class)
                .when(answerService).uploadEssay(TEST_ID, ESSAY_TEXT);

        Assertions.assertThrows(QuestionNotFoundException.class,
                () -> answerController.uploadEssay(TEST_ID, ESSAY_TEXT));
    }

    @Test
    void uploadEssaySavingError() {
        Mockito.doThrow(FileSavingFailedException.class)
                .when(answerService).uploadEssay(TEST_ID, ESSAY_TEXT);

        Assertions.assertThrows(FileSavingFailedException.class,
                () -> answerController.uploadEssay(TEST_ID, ESSAY_TEXT));
    }

    @Test
    void uploadEssaySuccess() {
        Mockito.when(answerService.uploadEssay(TEST_ID, ESSAY_TEXT)).thenReturn(ESSAY_TEXT);
        Assertions.assertEquals(ESSAY_TEXT, answerController.uploadEssay(TEST_ID, ESSAY_TEXT));
    }

    @Test
    void uploadSpeakingSuccess() {
        Mockito.when(answerService.uploadSpeaking(any(), any())).thenReturn(URL);
        Assertions.assertEquals(URL, answerController.uploadSpeaking(file, TEST_ID));
    }

    @Test
    void uploadSpeakingSavingError() {
        Mockito.doThrow(FileSavingFailedException.class)
                .when(answerService).uploadSpeaking(file, TEST_ID);
        Assertions.assertThrows(FileSavingFailedException.class,
                () -> answerController.uploadSpeaking(file, TEST_ID));
    }

    @Test
    void uploadSpeakingQuestionNotFound() {
        Mockito.doThrow(QuestionNotFoundException.class)
                .when(answerService).uploadSpeaking(file, TEST_ID);
        Assertions.assertThrows(QuestionNotFoundException.class,
                () -> answerController.uploadSpeaking(file, TEST_ID));
    }
}
