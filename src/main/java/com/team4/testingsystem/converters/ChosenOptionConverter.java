package com.team4.testingsystem.converters;

import com.team4.testingsystem.dto.ChosenOptionDTO;
import com.team4.testingsystem.entities.Answer;
import com.team4.testingsystem.entities.ChosenOption;
import com.team4.testingsystem.entities.Question;
import com.team4.testingsystem.entities.Test;
import com.team4.testingsystem.entities.TestQuestionID;
import com.team4.testingsystem.exceptions.AnswerNotFoundException;
import com.team4.testingsystem.services.AnswerService;
import com.team4.testingsystem.services.QuestionService;
import com.team4.testingsystem.services.TestsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ChosenOptionConverter {
    private final TestsService testsService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    public ChosenOption convertToEntity(ChosenOptionDTO chosenOptionDTO) {
        Test test = testsService.getById(chosenOptionDTO.getTestId());
        Question question = questionService.getById(chosenOptionDTO.getQuestionId());
        Answer answer = answerService.getById(chosenOptionDTO.getAnswerId());

        if (!answer.getQuestion().equals(question)) {
            throw new AnswerNotFoundException();
        }

        TestQuestionID id = new TestQuestionID(test, question);
        return new ChosenOption(id, answer);
    }
}
