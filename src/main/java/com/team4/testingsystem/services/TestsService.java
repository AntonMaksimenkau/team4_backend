package com.team4.testingsystem.services;

import com.team4.testingsystem.entities.Test;

public interface TestsService {

    Iterable<Test> getAll();

    Test getById(long id);

    long createForUser(long userId);

    void start(long id);

    void finish(long id, int evaluation);

    void updateEvaluation(long id, int newEvaluation);

    void removeById(long id);

    void assignCoach(long id, long coachId);

    void deassignCoach(long id);
}