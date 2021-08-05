package com.team4.testingsystem.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team4.testingsystem.dto.CoachGradeDTO;
import com.team4.testingsystem.entities.CoachGrade;
import com.team4.testingsystem.entities.Question;
import com.team4.testingsystem.entities.User;
import com.team4.testingsystem.repositories.AnswerRepository;
import com.team4.testingsystem.repositories.CoachGradeRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
class CoachGradeControllerIntegrationTest {
    private final MockMvc mockMvc;

    private final UsersRepository usersRepository;
    private final QuestionRepository questionRepository;
    private final TestsRepository testsRepository;
    private final CoachGradeRepository gradeRepository;
    private final AnswerRepository answerRepository;

    private final ObjectMapper objectMapper;

    private User user;
    private CustomUserDetails userDetails;

    @Autowired
    CoachGradeControllerIntegrationTest(MockMvc mockMvc,
                                        UsersRepository usersRepository,
                                        QuestionRepository questionRepository,
                                        TestsRepository testsRepository,
                                        CoachGradeRepository gradeRepository,
                                        AnswerRepository answerRepository, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.usersRepository = usersRepository;
        this.questionRepository = questionRepository;
        this.testsRepository = testsRepository;
        this.gradeRepository = gradeRepository;
        this.answerRepository = answerRepository;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void init() {
        answerRepository.deleteAll();
        user = usersRepository.findByLogin("rus_user@northsixty.com").orElseThrow();
        userDetails = new CustomUserDetails(user);
    }

    @AfterEach
    void destroy() {
        gradeRepository.deleteAll();
        answerRepository.deleteAll();
        questionRepository.deleteAll();
        testsRepository.deleteAll();
    }

    @Test
    void getGradesTestNotFound() throws Exception {
        mockMvc.perform(get("/grades/101")
                .with(user(userDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getGradesEmptyList() throws Exception {
        com.team4.testingsystem.entities.Test test = EntityCreatorUtil.createTest(user);
        testsRepository.save(test);

        MvcResult mvcResult = mockMvc.perform(get("/grades/{testId}", test.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        List<CoachGradeDTO> grades = objectMapper.readValue(response, new TypeReference<>() {});

        Assertions.assertTrue(grades.isEmpty());
    }

    @Test
    void getGradesOneGrade() throws Exception {
        com.team4.testingsystem.entities.Test test = EntityCreatorUtil.createTest(user);
        testsRepository.save(test);

        Question question = EntityCreatorUtil.createQuestion(user);
        questionRepository.save(question);

        CoachGrade grade = new CoachGrade(test, question, 8);
        gradeRepository.save(grade);

        MvcResult mvcResult = mockMvc.perform(get("/grades/{testId}", test.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        List<CoachGradeDTO> grades = objectMapper.readValue(response, new TypeReference<>() {});

        Assertions.assertEquals(List.of(new CoachGradeDTO(grade)), grades);
    }

    @Test
    void getGradesTwoGrades() throws Exception {
        com.team4.testingsystem.entities.Test test = EntityCreatorUtil.createTest(user);
        testsRepository.save(test);

        Question question = EntityCreatorUtil.createQuestion(user);
        questionRepository.save(question);

        CoachGrade grade = new CoachGrade(test, question, 8);
        gradeRepository.save(grade);

        Question question2 = EntityCreatorUtil.createQuestion(user);
        questionRepository.save(question2);

        CoachGrade grade2 = new CoachGrade(test, question, 7);
        gradeRepository.save(grade2);

        MvcResult mvcResult = mockMvc.perform(get("/grades/{testId}", test.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        List<CoachGradeDTO> grades = objectMapper.readValue(response, new TypeReference<>() {});

        Assertions.assertEquals(2, grades.size());
        Assertions.assertTrue(grades.contains(new CoachGradeDTO(grade)));
        Assertions.assertTrue(grades.contains(new CoachGradeDTO(grade2)));
    }

    @Test
    void createGradeTestNotFound() throws Exception {
        CoachGradeDTO gradeDTO = CoachGradeDTO.builder()
                .testId(200L)
                .questionId(300L)
                .grade(10)
                .build();

        mockMvc.perform(post("/grades/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gradeDTO))
                .with(user(userDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createGradeQuestionNotFound() throws Exception {
        com.team4.testingsystem.entities.Test test = EntityCreatorUtil.createTest(user);
        testsRepository.save(test);

        CoachGradeDTO gradeDTO = CoachGradeDTO.builder()
                .testId(test.getId())
                .questionId(301L)
                .grade(10)
                .build();

        mockMvc.perform(post("/grades/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gradeDTO))
                .with(user(userDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createGradeSuccess() throws Exception {
        com.team4.testingsystem.entities.Test test = EntityCreatorUtil.createTest(user);
        testsRepository.save(test);

        Question question = EntityCreatorUtil.createQuestion(user);
        questionRepository.save(question);

        CoachGradeDTO gradeDTO = CoachGradeDTO.builder()
                .testId(test.getId())
                .questionId(question.getId())
                .grade(10)
                .build();

        mockMvc.perform(post("/grades/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gradeDTO))
                .with(user(userDetails)))
                .andExpect(status().isOk());

        Optional<CoachGrade> grade = gradeRepository.findByTestAndQuestion(test, question);
        Assertions.assertTrue(grade.isPresent());
        Assertions.assertEquals(gradeDTO.getGrade(), grade.get().getGrade());
    }

    @Test
    void createGradeAlreadyExists() throws Exception {
        com.team4.testingsystem.entities.Test test = EntityCreatorUtil.createTest(user);
        testsRepository.save(test);

        Question question = EntityCreatorUtil.createQuestion(user);
        questionRepository.save(question);

        CoachGrade grade = new CoachGrade(test, question, 10);
        gradeRepository.save(grade);

        grade.setGrade(1);

        mockMvc.perform(post("/grades/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CoachGradeDTO(grade)))
                .with(user(userDetails)))
                .andExpect(status().isConflict());

        Optional<CoachGrade> savedGrade = gradeRepository.findByTestAndQuestion(test, question);
        Assertions.assertTrue(savedGrade.isPresent());
        Assertions.assertEquals(10, savedGrade.get().getGrade());
    }

    @Test
    void updateGradeTestNotFound() throws Exception {
        CoachGradeDTO gradeDTO = CoachGradeDTO.builder()
                .testId(400L)
                .questionId(500L)
                .grade(10)
                .build();

        mockMvc.perform(put("/grades/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gradeDTO))
                .with(user(userDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateGradeQuestionNotFound() throws Exception {
        com.team4.testingsystem.entities.Test test = EntityCreatorUtil.createTest(user);
        testsRepository.save(test);

        CoachGradeDTO gradeDTO = CoachGradeDTO.builder()
                .testId(test.getId())
                .questionId(501L)
                .grade(10)
                .build();

        mockMvc.perform(put("/grades/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gradeDTO))
                .with(user(userDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateGradeNotExists() throws Exception {
        com.team4.testingsystem.entities.Test test = EntityCreatorUtil.createTest(user);
        testsRepository.save(test);

        Question question = EntityCreatorUtil.createQuestion(user);
        questionRepository.save(question);

        CoachGradeDTO gradeDTO = CoachGradeDTO.builder()
                .testId(test.getId())
                .questionId(question.getId())
                .grade(10)
                .build();

        mockMvc.perform(put("/grades/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gradeDTO))
                .with(user(userDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateGradeSuccess() throws Exception {
        com.team4.testingsystem.entities.Test test = EntityCreatorUtil.createTest(user);
        testsRepository.save(test);

        Question question = EntityCreatorUtil.createQuestion(user);
        questionRepository.save(question);

        CoachGradeDTO gradeDTO = CoachGradeDTO.builder()
                .testId(test.getId())
                .questionId(question.getId())
                .grade(10)
                .build();

        CoachGrade grade = new CoachGrade(test, question, 2);
        gradeRepository.save(grade);

        mockMvc.perform(put("/grades/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gradeDTO))
                .with(user(userDetails)))
                .andExpect(status().isOk());

        Optional<CoachGrade> savedGrade = gradeRepository.findByTestAndQuestion(test, question);
        Assertions.assertTrue(savedGrade.isPresent());
        Assertions.assertEquals(10, savedGrade.get().getGrade());
    }
}
