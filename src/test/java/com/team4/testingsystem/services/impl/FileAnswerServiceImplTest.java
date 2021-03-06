package com.team4.testingsystem.services.impl;

import com.team4.testingsystem.entities.FileAnswer;
import com.team4.testingsystem.entities.Question;
import com.team4.testingsystem.entities.Test;
import com.team4.testingsystem.entities.TestQuestionID;
import com.team4.testingsystem.enums.Modules;
import com.team4.testingsystem.enums.Status;
import com.team4.testingsystem.exceptions.FileAnswerNotFoundException;
import com.team4.testingsystem.exceptions.FileLoadingFailedException;
import com.team4.testingsystem.exceptions.FileSavingFailedException;
import com.team4.testingsystem.exceptions.QuestionNotFoundException;
import com.team4.testingsystem.exceptions.TestNotFoundException;
import com.team4.testingsystem.exceptions.TooLongEssayException;
import com.team4.testingsystem.repositories.FileAnswerRepository;
import com.team4.testingsystem.security.CustomUserDetails;
import com.team4.testingsystem.services.QuestionService;
import com.team4.testingsystem.services.ResourceStorageService;
import com.team4.testingsystem.services.RestrictionsService;
import com.team4.testingsystem.services.TestsService;
import com.team4.testingsystem.utils.EntityCreatorUtil;
import com.team4.testingsystem.utils.jwt.JwtTokenUtil;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class FileAnswerServiceImplTest {

    @Mock
    private TestsService testsService;

    @Mock
    private QuestionService questionService;

    @Mock
    private ResourceStorageService resourceStorageService;

    @Mock
    private FileAnswerRepository fileAnswerRepository;

    @Mock
    private FileAnswer fileAnswer;

    @Mock
    private Test test;

    @Mock
    private Question question;

    @Mock
    private MultipartFile file;

    @Mock
    private CustomUserDetails userDetails;

    @Mock
    private RestrictionsService restrictionsService;

    @InjectMocks
    private FileAnswerServiceImpl fileAnswerService;

    private static final Long TEST_ID = 1L;
    private static final Long QUESTION_ID = 2L;
    private static final String URL = "url";
    private static final String ESSAY_TEXT = "essay text example";

    @org.junit.jupiter.api.Test
    void getUrlNotFound() {
        Mockito.when(fileAnswerRepository.findByTestAndQuestionId(TEST_ID, QUESTION_ID))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(FileAnswerNotFoundException.class,
                () -> fileAnswerService.getUrl(TEST_ID, QUESTION_ID));
    }

    @org.junit.jupiter.api.Test
    void getUrlSuccess() {
        Mockito.when(fileAnswerRepository.findByTestAndQuestionId(TEST_ID, QUESTION_ID))
                .thenReturn(Optional.of(fileAnswer));
        Mockito.when(fileAnswer.getUrl()).thenReturn(URL);

        Assertions.assertEquals(URL, fileAnswerService.getUrl(TEST_ID, QUESTION_ID));
    }

    @org.junit.jupiter.api.Test
    void removeTestNotFound() {
        Mockito.when(testsService.getById(TEST_ID)).thenThrow(TestNotFoundException.class);

        Assertions.assertThrows(TestNotFoundException.class,
                () -> fileAnswerService.remove(TEST_ID, QUESTION_ID));
    }

    @org.junit.jupiter.api.Test
    void removeQuestionNotFound() {
        Mockito.when(testsService.getById(TEST_ID)).thenReturn(test);
        Mockito.when(questionService.getById(QUESTION_ID)).thenThrow(QuestionNotFoundException.class);

        Assertions.assertThrows(QuestionNotFoundException.class,
                () -> fileAnswerService.remove(TEST_ID, QUESTION_ID));
    }

    @org.junit.jupiter.api.Test
    void removeSuccess() {
        Mockito.when(testsService.getById(TEST_ID)).thenReturn(test);
        Mockito.when(questionService.getById(QUESTION_ID)).thenReturn(question);

        fileAnswerService.remove(TEST_ID, QUESTION_ID);

        ArgumentCaptor<TestQuestionID> captor = ArgumentCaptor.forClass(TestQuestionID.class);
        Mockito.verify(fileAnswerRepository).deleteById(captor.capture());

        Assertions.assertEquals(test, captor.getValue().getTest());
        Assertions.assertEquals(question, captor.getValue().getQuestion());
    }

    @org.junit.jupiter.api.Test
    void downloadSpeakingSuccess() {
        Mockito.when(questionService
                .getQuestionByTestIdAndModule(any(), any())).thenReturn(question);
        Mockito.when(fileAnswerRepository
                .findByTestAndQuestionId(any(), any())).thenReturn(Optional.of(fileAnswer));
        Mockito.when(fileAnswerService.getUrl(any(), any())).thenReturn(URL);
        Assertions.assertEquals(URL, fileAnswerService.downloadSpeaking(EntityCreatorUtil.ID));
    }

    @org.junit.jupiter.api.Test
    void downloadSpeakingQuestionNotFound() {
        Mockito.doThrow(QuestionNotFoundException.class)
                .when(questionService).getQuestionByTestIdAndModule(TEST_ID, Modules.SPEAKING);
        Assertions.assertThrows(QuestionNotFoundException.class,
                () -> fileAnswerService.downloadSpeaking(TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void downloadSpeakingFileAnswerNotFound() {
        Mockito.doThrow(FileAnswerNotFoundException.class)
                .when(questionService).getQuestionByTestIdAndModule(TEST_ID, Modules.SPEAKING);
        Assertions.assertThrows(FileAnswerNotFoundException.class,
                () -> fileAnswerService.downloadSpeaking(TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void downloadEssayQuestionNotFound() {
        Mockito.when(questionService.getQuestionByTestIdAndModule(TEST_ID, Modules.ESSAY))
                .thenThrow(QuestionNotFoundException.class);

        Assertions.assertThrows(QuestionNotFoundException.class,
                () -> fileAnswerService.downloadEssay(TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void downloadEssayFileAnswerNotFound() {
        Mockito.when(questionService.getQuestionByTestIdAndModule(TEST_ID, Modules.ESSAY))
                .thenReturn(question);
        Mockito.when(question.getId()).thenReturn(QUESTION_ID);
        Mockito.when(fileAnswerRepository.findByTestAndQuestionId(TEST_ID, QUESTION_ID))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(FileAnswerNotFoundException.class,
                () -> fileAnswerService.downloadEssay(TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void downloadEssayLoadingError() {
        Mockito.when(questionService.getQuestionByTestIdAndModule(TEST_ID, Modules.ESSAY))
                .thenReturn(question);
        Mockito.when(question.getId()).thenReturn(QUESTION_ID);
        Mockito.when(fileAnswerRepository.findByTestAndQuestionId(TEST_ID, QUESTION_ID))
                .thenReturn(Optional.of(fileAnswer));
        Mockito.when(fileAnswer.getUrl()).thenReturn(URL);

        Mockito.when(resourceStorageService.load(URL)).thenThrow(FileLoadingFailedException.class);

        Assertions.assertThrows(FileLoadingFailedException.class,
                () -> fileAnswerService.downloadEssay(TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void downloadEssaySuccess() {
        Mockito.when(questionService.getQuestionByTestIdAndModule(TEST_ID, Modules.ESSAY))
                .thenReturn(question);
        Mockito.when(question.getId()).thenReturn(QUESTION_ID);
        Mockito.when(fileAnswerRepository.findByTestAndQuestionId(TEST_ID, QUESTION_ID))
                .thenReturn(Optional.of(fileAnswer));
        Mockito.when(fileAnswer.getUrl()).thenReturn(URL);

        InputStream inputStream = IOUtils.toInputStream(ESSAY_TEXT, StandardCharsets.UTF_8);
        Mockito.when(resourceStorageService.load(URL)).thenReturn(new InputStreamResource(inputStream));

        Assertions.assertEquals(ESSAY_TEXT, fileAnswerService.downloadEssay(TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void uploadEssayTestNotFound() {
        Mockito.when(testsService.getById(TEST_ID)).thenThrow(TestNotFoundException.class);

        Assertions.assertThrows(TestNotFoundException.class,
                () -> fileAnswerService.uploadEssay(TEST_ID, ESSAY_TEXT));
    }

    @org.junit.jupiter.api.Test
    void uploadEssayQuestionNotFound() {
        try (MockedStatic<JwtTokenUtil> mockJwtTokenUtil = Mockito.mockStatic(JwtTokenUtil.class)) {
            mockJwtTokenUtil.when(JwtTokenUtil::extractUserDetails).thenReturn(userDetails);
            Mockito.when(userDetails.getId()).thenReturn(1L);

            Mockito.when(testsService.getById(TEST_ID)).thenReturn(test);
            Mockito.when(questionService.getQuestionByTestIdAndModule(TEST_ID, Modules.ESSAY))
                    .thenThrow(QuestionNotFoundException.class);

            Assertions.assertThrows(QuestionNotFoundException.class,
                    () -> fileAnswerService.uploadEssay(TEST_ID, ESSAY_TEXT));
        }
    }

    @org.junit.jupiter.api.Test
    void uploadEssaySavingError() {
        try (MockedStatic<JwtTokenUtil> mockJwtTokenUtil = Mockito.mockStatic(JwtTokenUtil.class)) {
            mockJwtTokenUtil.when(JwtTokenUtil::extractUserDetails).thenReturn(userDetails);
            Mockito.when(userDetails.getId()).thenReturn(1L);

            Mockito.when(testsService.getById(TEST_ID)).thenReturn(test);
            Mockito.when(questionService.getQuestionByTestIdAndModule(TEST_ID, Modules.ESSAY)).thenReturn(question);
            Mockito.when(resourceStorageService.upload(Mockito.any(), Mockito.eq(Modules.ESSAY), Mockito.eq(TEST_ID)))
                    .thenThrow(FileSavingFailedException.class);

            Assertions.assertThrows(FileSavingFailedException.class,
                    () -> fileAnswerService.uploadEssay(TEST_ID, ESSAY_TEXT));
        }
    }

    @org.junit.jupiter.api.Test
    void uploadEssayFailTooLong() {
        try (MockedStatic<JwtTokenUtil> mockJwtTokenUtil = Mockito.mockStatic(JwtTokenUtil.class)) {
            mockJwtTokenUtil.when(JwtTokenUtil::extractUserDetails).thenReturn(userDetails);
            Mockito.when(userDetails.getId()).thenReturn(1L);

            Mockito.when(testsService.getById(TEST_ID)).thenReturn(test);
            Mockito.when(questionService.getQuestionByTestIdAndModule(TEST_ID, Modules.ESSAY)).thenReturn(question);

            Assertions.assertThrows(TooLongEssayException.class,
                    () -> fileAnswerService.uploadEssay(TEST_ID, "1".repeat(513)));
        }
    }

    @org.junit.jupiter.api.Test
    void uploadEssaySuccess() {
        try (MockedStatic<JwtTokenUtil> mockJwtTokenUtil = Mockito.mockStatic(JwtTokenUtil.class)) {
            mockJwtTokenUtil.when(JwtTokenUtil::extractUserDetails).thenReturn(userDetails);
            Mockito.when(userDetails.getId()).thenReturn(1L);

            Mockito.when(testsService.getById(TEST_ID)).thenReturn(test);
            Mockito.when(questionService.getQuestionByTestIdAndModule(TEST_ID, Modules.ESSAY)).thenReturn(question);
            Mockito.when(resourceStorageService.upload(Mockito.any(), Mockito.eq(Modules.ESSAY), Mockito.eq(TEST_ID)))
                    .thenReturn(URL);
            Mockito.when(fileAnswerRepository.findById(any())).thenReturn(Optional.of(fileAnswer));
            Mockito.when(fileAnswerRepository.existsById(any())).thenReturn(true);
            fileAnswerService.uploadEssay(TEST_ID, ESSAY_TEXT);

            Mockito.verify(restrictionsService).checkOwnerIsCurrentUser(test, userDetails.getId());
            Mockito.verify(restrictionsService).checkStatus(test, Status.STARTED);
            Mockito.verify(fileAnswerRepository).updateUrl(any(), any());

            Assertions.assertEquals(fileAnswer,  fileAnswerService.uploadEssay(TEST_ID, ESSAY_TEXT));
        }
    }

    @org.junit.jupiter.api.Test
    void uploadEssaySuccessTo512Symbols() {
        try (MockedStatic<JwtTokenUtil> mockJwtTokenUtil = Mockito.mockStatic(JwtTokenUtil.class)) {
            mockJwtTokenUtil.when(JwtTokenUtil::extractUserDetails).thenReturn(userDetails);
            Mockito.when(userDetails.getId()).thenReturn(1L);

            Mockito.when(testsService.getById(TEST_ID)).thenReturn(test);
            Mockito.when(questionService.getQuestionByTestIdAndModule(TEST_ID, Modules.ESSAY)).thenReturn(question);
            Mockito.when(resourceStorageService.upload(Mockito.any(), Mockito.eq(Modules.ESSAY), Mockito.eq(TEST_ID)))
                    .thenReturn(URL);
            Mockito.when(fileAnswerRepository.existsById(any())).thenReturn(false);
            Mockito.when(fileAnswerRepository.save(any())).thenReturn(fileAnswer);

            Assertions.assertEquals(fileAnswer, fileAnswerService.uploadEssay(TEST_ID, "1".repeat(512)));
        }
    }

    @org.junit.jupiter.api.Test
    void uploadSpeakingSuccess() {
        try (MockedStatic<JwtTokenUtil> mockJwtTokenUtil = Mockito.mockStatic(JwtTokenUtil.class)) {
            mockJwtTokenUtil.when(JwtTokenUtil::extractUserDetails).thenReturn(userDetails);
            Mockito.when(userDetails.getId()).thenReturn(1L);
            FileAnswer fileAnswer = new FileAnswer();
            Mockito.when(testsService.getById(TEST_ID)).thenReturn(test);
            Mockito.when(questionService.getQuestionByTestIdAndModule(TEST_ID, Modules.SPEAKING)).thenReturn(question);
            Mockito.when(resourceStorageService.upload(Mockito.any(), Mockito.eq(Modules.SPEAKING), Mockito.eq(TEST_ID)))
                    .thenReturn(URL);
            Mockito.when(fileAnswerRepository.existsById(any())).thenReturn(false);
            Mockito.when(fileAnswerRepository.save(any())).thenReturn(fileAnswer);
            fileAnswerService.uploadSpeaking(file, TEST_ID);

            Mockito.verify(restrictionsService).checkOwnerIsCurrentUser(test, userDetails.getId());
            Mockito.verify(restrictionsService).checkStatus(test, Status.STARTED);
            Assertions.assertEquals(fileAnswer,  fileAnswerService.uploadSpeaking(file, TEST_ID));
        }
    }

    @org.junit.jupiter.api.Test
    void uploadSpeakingSuccessIfExist() {
        try (MockedStatic<JwtTokenUtil> mockJwtTokenUtil = Mockito.mockStatic(JwtTokenUtil.class)) {
            mockJwtTokenUtil.when(JwtTokenUtil::extractUserDetails).thenReturn(userDetails);
            Mockito.when(userDetails.getId()).thenReturn(1L);

            Mockito.when(testsService.getById(TEST_ID)).thenReturn(test);
            Mockito.when(questionService.getQuestionByTestIdAndModule(TEST_ID, Modules.SPEAKING)).thenReturn(question);
            Mockito.when(resourceStorageService.upload(Mockito.any(), Mockito.eq(Modules.SPEAKING), Mockito.eq(TEST_ID)))
                    .thenReturn(URL);
            Mockito.when(fileAnswerRepository.findById(any())).thenReturn(Optional.of(fileAnswer));
            Mockito.when(fileAnswerRepository.existsById(any())).thenReturn(true);
            fileAnswerService.uploadSpeaking(file, TEST_ID);

            Mockito.verify(fileAnswerRepository).updateUrl(any(), any());
            Assertions.assertEquals(fileAnswer,  fileAnswerService.uploadEssay(TEST_ID, ESSAY_TEXT));
        }
    }


    @org.junit.jupiter.api.Test
    void uploadSpeakingTestNotFound() {
        Mockito.when(testsService.getById(TEST_ID)).thenThrow(TestNotFoundException.class);

        Assertions.assertThrows(TestNotFoundException.class,
                () -> fileAnswerService.uploadSpeaking(file, TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void uploadSpeakingQuestionNotFound() {
        try (MockedStatic<JwtTokenUtil> mockJwtTokenUtil = Mockito.mockStatic(JwtTokenUtil.class)) {
            mockJwtTokenUtil.when(JwtTokenUtil::extractUserDetails).thenReturn(userDetails);
            Mockito.when(userDetails.getId()).thenReturn(1L);

            Mockito.when(testsService.getById(TEST_ID)).thenReturn(test);
            Mockito.when(questionService.getQuestionByTestIdAndModule(TEST_ID, Modules.SPEAKING))
                    .thenThrow(QuestionNotFoundException.class);

            Assertions.assertThrows(QuestionNotFoundException.class,
                    () -> fileAnswerService.uploadSpeaking(file, TEST_ID));
        }
    }
}
