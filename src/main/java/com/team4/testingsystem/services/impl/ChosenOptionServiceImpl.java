package com.team4.testingsystem.services.impl;

import com.team4.testingsystem.entities.ChosenOption;
import com.team4.testingsystem.entities.Question;
import com.team4.testingsystem.entities.Test;
import com.team4.testingsystem.enums.Status;
import com.team4.testingsystem.exceptions.ChosenOptionBadRequestException;
import com.team4.testingsystem.exceptions.ChosenOptionNotFoundException;
import com.team4.testingsystem.repositories.ChosenOptionRepository;
import com.team4.testingsystem.services.ChosenOptionService;
import com.team4.testingsystem.services.RestrictionsService;
import com.team4.testingsystem.utils.jwt.JwtTokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.persistence.EntityNotFoundException;

@Service
@AllArgsConstructor
public class ChosenOptionServiceImpl implements ChosenOptionService {

    private final ChosenOptionRepository chosenOptionRepository;
    private final RestrictionsService restrictionsService;

    @Override
    public ChosenOption getByTestAndQuestionId(Long testId, Long questionId) {
        return chosenOptionRepository.findByTestAndQuestionId(testId, questionId)
                .orElseThrow(ChosenOptionNotFoundException::new);
    }

    @Override
    public List<ChosenOption> getAllByTestId(Long testId) {
        return chosenOptionRepository.findByTestId(testId);
    }

    @Override
    public void saveAll(List<ChosenOption> chosenOptions) {

        Long currentUserId = JwtTokenUtil.extractUserDetails().getId();

        chosenOptions.parallelStream().forEach(item -> {
                Test test = item.getId().getTest();
                Question question = item.getId().getQuestion();

                restrictionsService.checkOwnerIsCurrentUser(test, currentUserId);

                restrictionsService.checkStatus(test, Status.STARTED);

                restrictionsService.checkTestContainsQuestion(test, question);
            }
        );

        try {
            chosenOptionRepository.saveAll(chosenOptions);
        } catch (EntityNotFoundException exception) {
            throw new ChosenOptionBadRequestException();
        }
    }
}
