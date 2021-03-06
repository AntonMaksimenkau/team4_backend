package com.team4.testingsystem.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team4.testingsystem.dto.ErrorReportDTO;
import com.team4.testingsystem.entities.ErrorReport;
import com.team4.testingsystem.entities.Level;
import com.team4.testingsystem.entities.Question;
import com.team4.testingsystem.entities.TestQuestionID;
import com.team4.testingsystem.entities.User;
import com.team4.testingsystem.repositories.AnswerRepository;
import com.team4.testingsystem.enums.Levels;
import com.team4.testingsystem.repositories.ContentFilesRepository;
import com.team4.testingsystem.repositories.ErrorReportsRepository;
import com.team4.testingsystem.repositories.LevelRepository;
import com.team4.testingsystem.repositories.QuestionRepository;
import com.team4.testingsystem.repositories.TestsRepository;
import com.team4.testingsystem.repositories.UsersRepository;
import com.team4.testingsystem.security.CustomUserDetails;
import com.team4.testingsystem.utils.EntityCreatorUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
class ErrorReportsControllerIntegrationTest {

    private final long BAD_TEST_ID = 42L;

    private final long BAD_QUESTION_ID = 42L;

    private final String GOOD_REPORT_BODY = "Good report body";
    private final String BAD_REPORT_BODY = "Bad report body";
    private final String NEW_REPORT_BODY = "New report body";

    private final MockMvc mockMvc;

    private final LevelRepository levelRepository;
    private final UsersRepository usersRepository;
    private final QuestionRepository questionRepository;
    private final TestsRepository testsRepository;
    private final AnswerRepository answerRepository;
    private final ErrorReportsRepository errorReportsRepository;
    private final ContentFilesRepository contentFilesRepository;
    private final ObjectMapper objectMapper;

    private User user;
    private CustomUserDetails userDetails;
    private Level level;

    @Autowired
    ErrorReportsControllerIntegrationTest(MockMvc mockMvc,
                                          LevelRepository levelRepository,
                                          UsersRepository usersRepository,
                                          QuestionRepository questionRepository,
                                          TestsRepository testsRepository,
                                          AnswerRepository answerRepository,
                                          ErrorReportsRepository errorReportsRepository,
                                          ContentFilesRepository contentFilesRepository,
                                          ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.levelRepository = levelRepository;
        this.usersRepository = usersRepository;
        this.questionRepository = questionRepository;
        this.testsRepository = testsRepository;
        this.answerRepository = answerRepository;
        this.errorReportsRepository = errorReportsRepository;
        this.contentFilesRepository = contentFilesRepository;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void init() {
        answerRepository.deleteAll();
        user = usersRepository.findByLogin("rus_user@northsixty.com").orElseThrow();
        userDetails = new CustomUserDetails(user);
        level = levelRepository.findByName(Levels.A1.name()).orElseThrow();
    }

    @AfterEach
    void destroy() {
        errorReportsRepository.deleteAll();
        contentFilesRepository.deleteAll();
        answerRepository.deleteAll();
        testsRepository.deleteAll();
        questionRepository.deleteAll();
    }

    @Test
    void addSuccess() throws Exception {
        com.team4.testingsystem.entities.Test test = EntityCreatorUtil.createTest(user, level);

        Question question = EntityCreatorUtil.createQuestion(user);
        questionRepository.save(question);

        testsRepository.save(test);

        question.setTests(List.of(test));

        questionRepository.save(question);

        test.setQuestions(List.of(question));

        testsRepository.save(test);

        TestQuestionID testQuestionID = new TestQuestionID(test, question);

        ErrorReportDTO errorReportDTO = EntityCreatorUtil
                .createErrorReportDTO(GOOD_REPORT_BODY, question.getId(), test.getId());

        mockMvc.perform(post("/error_reports/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(errorReportDTO))
                .with(user(userDetails)))
                .andExpect(status().isOk());

        Optional<ErrorReport> report = errorReportsRepository.findById(testQuestionID);
        Assertions.assertTrue(report.isPresent());
        Assertions.assertEquals(GOOD_REPORT_BODY, report.get().getReportBody());
    }


    @Test
    void addFailReportNotFound() throws Exception {

        ErrorReportDTO errorReportDTO = EntityCreatorUtil
                .createErrorReportDTO(BAD_REPORT_BODY, BAD_QUESTION_ID, BAD_TEST_ID);

        mockMvc.perform(post("/error_reports/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(errorReportDTO))
                .with(user(userDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReportBodySuccess() throws Exception {
        com.team4.testingsystem.entities.Test test = EntityCreatorUtil.createTest(user, level);

        Question question = EntityCreatorUtil.createQuestion(user);
        questionRepository.save(question);

        testsRepository.save(test);

        question.setTests(List.of(test));

        questionRepository.save(question);

        test.setQuestions(List.of(question));

        testsRepository.save(test);

        TestQuestionID testQuestionID = new TestQuestionID(test, question);

        ErrorReport errorReport = new ErrorReport(testQuestionID, GOOD_REPORT_BODY);
        errorReportsRepository.save(errorReport);

        ErrorReportDTO errorReportDTO = EntityCreatorUtil
                .createErrorReportDTO(NEW_REPORT_BODY, question.getId(), test.getId());

        mockMvc.perform(post("/error_reports/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(errorReportDTO))
                .with(user(userDetails)))
                .andExpect(status().isOk());

        Optional<ErrorReport> report = errorReportsRepository.findById(testQuestionID);
        Assertions.assertEquals(NEW_REPORT_BODY, report.get().getReportBody());
    }


    @Test
    void removeSuccess() throws Exception {
        com.team4.testingsystem.entities.Test test = EntityCreatorUtil.createTest(user, level);

        Question question = EntityCreatorUtil.createQuestion(user);
        questionRepository.save(question);

        testsRepository.save(test);

        question.setTests(List.of(test));

        questionRepository.save(question);

        test.setQuestions(List.of(question));

        testsRepository.save(test);

        TestQuestionID testQuestionID = new TestQuestionID(test, question);

        ErrorReport errorReport = new ErrorReport(testQuestionID, GOOD_REPORT_BODY);
        errorReportsRepository.save(errorReport);


        mockMvc.perform(delete("/error_reports/")
                .param("testId", test.getId().toString())
                .param("questionId", question.getId().toString())
                .with(user(userDetails)))
                .andExpect(status().isOk());

        Optional<ErrorReport> report = errorReportsRepository.findById(testQuestionID);
        Assertions.assertTrue(report.isEmpty());
    }

    @Test
    void removeFail() throws Exception {
        mockMvc.perform(delete("/error_reports/")
                .param("testId", String.valueOf(BAD_TEST_ID))
                .param("questionId", String.valueOf(BAD_QUESTION_ID))
                .with(user(userDetails)))
                .andExpect(status().isNotFound());
    }

}
