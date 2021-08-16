package com.team4.testingsystem.services;

import com.team4.testingsystem.entities.Test;
import com.team4.testingsystem.entities.UserTest;
import com.team4.testingsystem.enums.Levels;
import com.team4.testingsystem.enums.Priority;
import com.team4.testingsystem.enums.Status;

import java.time.Instant;
import java.util.List;

public interface TestsService {

    Test getById(long id);

    List<Test> getByUserId(long userId);

    List<Test> getByStatuses(Status[] status);

    List<Test> getAllUnverifiedTestsByCoach(long coachId);

    List<UserTest> getAllUsersAndAssignedTests();

    List<Test> getTestsByUserIdAndLevel(long userId, Levels level);

    Test save(Test test);

    long startForUser(long userId, Levels level);

    long assignForUser(long userId, Levels level, Instant deadline, Priority priority);

    Test start(long id);

    void deassign(long id);

    void finish(long id, Instant finishDate);

    void update(long id);

    void assignCoach(long id, long coachId);

    void deassignCoach(long id);
}
