package com.team4.testingsystem.services.impl;

import com.team4.testingsystem.entities.CoachGrade;
import com.team4.testingsystem.entities.Question;
import com.team4.testingsystem.entities.Test;
import com.team4.testingsystem.exceptions.CoachGradeAlreadyExistsException;
import com.team4.testingsystem.exceptions.GradeNotFoundException;
import com.team4.testingsystem.repositories.CoachGradeRepository;
import com.team4.testingsystem.services.CoachGradeService;
import com.team4.testingsystem.services.QuestionService;
import com.team4.testingsystem.services.TestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CoachGradeServiceImpl implements CoachGradeService {
    private final CoachGradeRepository gradeRepository;

    private final TestsService testsService;
    private final QuestionService questionService;

    @Autowired
    public CoachGradeServiceImpl(CoachGradeRepository gradeRepository,
                                 TestsService testsService,
                                 QuestionService questionService) {
        this.gradeRepository = gradeRepository;
        this.testsService = testsService;
        this.questionService = questionService;
    }

    @Override
    public Collection<CoachGrade> getGradesByTest(Long testId) {
        return gradeRepository.findAllByTest(testsService.getById(testId));
    }

    @Override
    public void createGrade(Long testId, Long questionId, Integer grade) {
        Test test = testsService.getById(testId);
        Question question = questionService.getById(questionId);

        if (gradeRepository.findByTestAndQuestion(test, question).isPresent()) {
            throw new CoachGradeAlreadyExistsException();
        }

        gradeRepository.save(new CoachGrade(test, question, grade));
    }

    @Override
    public void updateGrade(Long testId, Long questionId, Integer grade) {
        Test test = testsService.getById(testId);
        Question question = questionService.getById(questionId);

        if (gradeRepository.updateGrade(test, question, grade) == 0) {
            throw new GradeNotFoundException();
        }
    }
}
