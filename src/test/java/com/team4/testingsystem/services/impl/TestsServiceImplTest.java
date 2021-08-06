package com.team4.testingsystem.services.impl;

import com.team4.testingsystem.converters.TestConverter;
import com.team4.testingsystem.dto.TestDTO;

import com.team4.testingsystem.entities.Level;
import com.team4.testingsystem.entities.Test;
import com.team4.testingsystem.entities.User;
import com.team4.testingsystem.enums.Levels;
import com.team4.testingsystem.enums.Status;
import com.team4.testingsystem.exceptions.CoachAssignmentFailException;
import com.team4.testingsystem.exceptions.TestNotFoundException;
import com.team4.testingsystem.exceptions.UserNotFoundException;
import com.team4.testingsystem.repositories.TestsRepository;
import com.team4.testingsystem.services.LevelService;
import com.team4.testingsystem.services.TestEvaluationService;
import com.team4.testingsystem.services.UsersService;
import com.team4.testingsystem.utils.EntityCreatorUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TestsServiceImplTest {

    final long GOOD_TEST_ID = 1L;
    final long GOOD_USER_ID = 111L;
    final long BAD_TEST_ID = 42L;
    final long BAD_USER_ID = 424242L;

    @Mock
    Test test;

    @Mock
    Test.Builder builder;

    @Mock
    User user;

    @Mock
    UsersService usersService;

    @Mock
    LevelService levelService;

    @Mock
    TestsRepository testsRepository;

    @Mock
    TestGeneratingServiceImpl testGeneratingService;

    @Mock
    TestEvaluationService testEvaluationService;
    TestConverter testConverter;

    @InjectMocks
    TestsServiceImpl testsService;


    @org.junit.jupiter.api.Test
    void getAllSuccess() {
        List<Test> tests = new ArrayList<>();
        Mockito.when(testsRepository.findAll()).thenReturn(tests);

        Assertions.assertEquals(tests, testsService.getAll());
    }

    @org.junit.jupiter.api.Test
    void getByIdSuccess() {
        Mockito.when(testsRepository.findById(GOOD_TEST_ID)).thenReturn(Optional.of(test));
        Assertions.assertEquals(test, testsService.getById(GOOD_TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void getByIdFail() {
        Mockito.when(testsRepository.findById(BAD_TEST_ID)).thenThrow(TestNotFoundException.class);
        Assertions.assertThrows(TestNotFoundException.class, () -> testsService.getById(BAD_TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void getByUserIdSuccess() {
        List<Test> tests = new ArrayList<>();
        Mockito.when(usersService.getUserById(GOOD_USER_ID)).thenReturn(user);

        Mockito.when(testsRepository.getAllByUser(user)).thenReturn(tests);

        Assertions.assertEquals(tests, testsService.getByUserId(GOOD_USER_ID));

    }

    @org.junit.jupiter.api.Test
    void getByUserIdFailUserNotFound() {
        Mockito.when(usersService.getUserById(BAD_USER_ID)).thenThrow(UserNotFoundException.class);
        Assertions.assertThrows(UserNotFoundException.class, () -> testsService.getByUserId(BAD_USER_ID));
    }

    @org.junit.jupiter.api.Test
    void startWhenAssignFail() {
        Level level = EntityCreatorUtil.createLevel();
        Mockito.when(levelService.getLevelByName(level.getName())).thenReturn(level);
        Mockito.when(usersService.getUserById(BAD_USER_ID)).thenThrow(UserNotFoundException.class);

        Assertions.assertThrows(
                UserNotFoundException.class, () -> testsService.startForUser(BAD_USER_ID, Levels.A1));
    }

    @org.junit.jupiter.api.Test
    void startForUserSuccess() {
        Level level = EntityCreatorUtil.createLevel();
        Mockito.when(levelService.getLevelByName(level.getName())).thenReturn(level);

        Mockito.when(usersService.getUserById(GOOD_USER_ID)).thenReturn(user);

        try (MockedStatic<Test> builderMockedStatic = Mockito.mockStatic(Test.class)) {

            builderMockedStatic.when(Test::builder).thenReturn(builder);

            Mockito.when(builder.user(any())).thenReturn(builder);
            Mockito.when(builder.startedAt(any())).thenReturn(builder);
            Mockito.when(builder.status(any())).thenReturn(builder);
            Mockito.when(builder.level(any())).thenReturn(builder);
            Mockito.when(builder.build()).thenReturn(test);

            Mockito.when(test.getId()).thenReturn(1L);

            Assertions.assertEquals(1L, testsService.startForUser(GOOD_USER_ID, Levels.A1));
        }
    }

    @org.junit.jupiter.api.Test
    void assignWhenAssignFail() {
        Level level = EntityCreatorUtil.createLevel();
        LocalDateTime deadline = LocalDateTime.now();

        Mockito.when(levelService.getLevelByName(level.getName())).thenReturn(level);
        Mockito.when(usersService.getUserById(BAD_USER_ID)).thenThrow(UserNotFoundException.class);

        Assertions.assertThrows(UserNotFoundException.class,
                () -> testsService.assignForUser(BAD_USER_ID, Levels.A1, deadline));
    }

    @org.junit.jupiter.api.Test
    void assignForUserSuccess() {
        Level level = EntityCreatorUtil.createLevel();
        LocalDateTime deadline = LocalDateTime.now();

        Mockito.when(levelService.getLevelByName(level.getName())).thenReturn(level);
        Mockito.when(usersService.getUserById(GOOD_USER_ID)).thenReturn(user);

        try (MockedStatic<Test> builderMockedStatic = Mockito.mockStatic(Test.class)) {

            builderMockedStatic.when(Test::builder).thenReturn(builder);

            Mockito.when(builder.user(any())).thenReturn(builder);
            Mockito.when(builder.assignedAt(any())).thenReturn(builder);
            Mockito.when(builder.deadline(deadline)).thenReturn(builder);
            Mockito.when(builder.status(any())).thenReturn(builder);
            Mockito.when(builder.level(any())).thenReturn(builder);
            Mockito.when(builder.build()).thenReturn(test);

            Mockito.when(test.getId()).thenReturn(1L);

            Assertions.assertEquals(1L, testsService.assignForUser(GOOD_USER_ID, Levels.A1, deadline));
        }
    }

    @org.junit.jupiter.api.Test
    void startSuccess() {
        User user = EntityCreatorUtil.createUser();
        Level level = EntityCreatorUtil.createLevel();
        Test test = EntityCreatorUtil.createTest(user, level);
        TestDTO testDTO = EntityCreatorUtil.createTestDTO(test);

        Mockito.when(testsRepository.start(any(), anyLong())).thenReturn(1);
        Mockito.when(testsRepository.findById(GOOD_TEST_ID)).thenReturn(Optional.of(test));
        Mockito.when(testGeneratingService.formTest(any())).thenReturn(test);
        Mockito.when(testConverter.convertToDTO(test)).thenReturn(testDTO);
        TestDTO result = testsService.start(GOOD_TEST_ID);

        verify(testsRepository).start(any(LocalDateTime.class), anyLong());
        Assertions.assertDoesNotThrow(() -> testsService.start(GOOD_TEST_ID));
        Assertions.assertEquals(testDTO, result);
    }

    @org.junit.jupiter.api.Test
    void startFail() {
        Mockito.when(testsRepository.start(any(), anyLong())).thenReturn(0);

        Assertions.assertThrows(TestNotFoundException.class, () -> testsService.start(BAD_TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void finishSuccess() {
        Test test = new Test();

        Mockito.when(testsRepository.finish(any(), anyInt(), anyLong())).thenReturn(1);
        Mockito.when(testsRepository.findById(GOOD_TEST_ID)).thenReturn(Optional.of(test));
        Mockito.when(testEvaluationService.getEvaluationByTest(test)).thenReturn(anyInt());

        testsService.finish(GOOD_TEST_ID);

        verify(testsRepository).finish(any(LocalDateTime.class), anyInt(), anyLong());

        Assertions.assertDoesNotThrow(() -> testsService.finish(GOOD_TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void finishFail() {
        Test test = new Test();
        Mockito.when(testsRepository.findById(BAD_TEST_ID)).thenReturn(Optional.of(test));
        Mockito.when(testEvaluationService.getEvaluationByTest(test)).thenThrow(TestNotFoundException.class);

        Assertions.assertThrows(TestNotFoundException.class, () -> testsService.finish(BAD_TEST_ID));

        Mockito.when(testsRepository.finish(any(), anyInt(), anyLong())).thenReturn(0);

        Assertions.assertThrows(TestNotFoundException.class, () -> testsService.finish(BAD_TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void updateEvaluationSuccess() {
        Mockito.when(testsRepository.updateEvaluation(any(), anyInt(), anyLong())).thenReturn(1);

        testsService.updateEvaluation(GOOD_TEST_ID, 1);

        verify(testsRepository).updateEvaluation(any(LocalDateTime.class), anyInt(), anyLong());

        Assertions.assertDoesNotThrow(() -> testsService.updateEvaluation(GOOD_TEST_ID, 1));
    }

    @org.junit.jupiter.api.Test
    void updateEvaluationFail() {
        Mockito.when(testsRepository.updateEvaluation(any(), anyInt(), anyLong())).thenReturn(0);

        Assertions.assertThrows(TestNotFoundException.class,
                () -> testsService.updateEvaluation(BAD_TEST_ID, 42));
    }

    @org.junit.jupiter.api.Test
    void removeSuccess() {
        Mockito.when(testsRepository.removeById(GOOD_TEST_ID)).thenReturn(1);

        testsService.removeById(GOOD_TEST_ID);

        verify(testsRepository).removeById(GOOD_TEST_ID);

        Assertions.assertDoesNotThrow(() -> testsService.removeById(GOOD_TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void removeFail() {
        Mockito.when(testsRepository.removeById(BAD_TEST_ID)).thenReturn(0);

        Assertions.assertThrows(TestNotFoundException.class, () -> testsService.removeById(BAD_TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void saveSuccess() {
        Mockito.when(testsRepository.save(test)).thenReturn(test);
        Assertions.assertEquals(test, testsService.save(test));
    }

    @org.junit.jupiter.api.Test
    void saveFail() {
        Mockito.when(testsRepository.save(test)).thenThrow(RuntimeException.class);
        Assertions.assertThrows(RuntimeException.class, () -> testsService.save(test));
    }

    @org.junit.jupiter.api.Test
    void assignCoachSuccess() {
        Mockito.when(usersService.getUserById(GOOD_USER_ID)).thenReturn(user);

        Mockito.when(testsRepository.findById(GOOD_TEST_ID)).thenReturn(Optional.of(test));

        Mockito.when(test.getUser()).thenReturn(user);

        Mockito.when(user.getId()).thenReturn(GOOD_USER_ID + 1);

        testsService.assignCoach(GOOD_TEST_ID, GOOD_USER_ID);

        verify(testsRepository).assignCoach(user, GOOD_TEST_ID);

        Assertions.assertDoesNotThrow(() -> testsService.assignCoach(GOOD_TEST_ID, GOOD_USER_ID));
    }

    @org.junit.jupiter.api.Test
    void assignCoachFailUserNotFound() {
        Mockito.when(usersService.getUserById(BAD_USER_ID)).thenThrow(UserNotFoundException.class);

        Assertions.assertThrows(UserNotFoundException.class, () -> testsService.assignCoach(BAD_TEST_ID, BAD_USER_ID));
    }

    @org.junit.jupiter.api.Test
    void assignCoachFailTestNotFound() {
        Mockito.when(usersService.getUserById(GOOD_USER_ID)).thenReturn(user);

        Mockito.when(testsRepository.findById(GOOD_TEST_ID)).thenReturn(Optional.of(test));

        Mockito.when(test.getUser()).thenReturn(user);

        Mockito.when(user.getId()).thenReturn(GOOD_USER_ID);

        Assertions.assertThrows(CoachAssignmentFailException.class,
                () -> testsService.assignCoach(GOOD_TEST_ID, GOOD_USER_ID));
    }

    @org.junit.jupiter.api.Test
    void assignCoachFailSelfAssignment() {
        Mockito.when(usersService.getUserById(GOOD_USER_ID)).thenReturn(user);

        Mockito.when(testsRepository.findById(BAD_TEST_ID)).thenThrow(TestNotFoundException.class);

        Assertions.assertThrows(TestNotFoundException.class, () -> testsService.assignCoach(BAD_TEST_ID, GOOD_USER_ID));
    }

    @org.junit.jupiter.api.Test
    void deassignCoachSuccess() {
        Mockito.when(testsRepository.deassignCoach(GOOD_TEST_ID)).thenReturn(1);

        testsService.deassignCoach(GOOD_TEST_ID);

        verify(testsRepository).deassignCoach(GOOD_TEST_ID);

        Assertions.assertDoesNotThrow(() -> testsService.deassignCoach(GOOD_TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void deassignCoachFail() {
        Mockito.when(testsRepository.deassignCoach(BAD_TEST_ID)).thenReturn(0);

        Assertions.assertThrows(TestNotFoundException.class, () -> testsService.deassignCoach(BAD_TEST_ID));
    }

    @org.junit.jupiter.api.Test
    void getByStatus() {
        List<Test> tests = new ArrayList<>();
        Status[] statuses = {Status.COMPLETED, Status.IN_VERIFICATION};
        Mockito.when(testsRepository.getByStatuses(any())).thenReturn(tests);
        Assertions.assertEquals(tests, testsService.getByStatuses(statuses));
    }
}
