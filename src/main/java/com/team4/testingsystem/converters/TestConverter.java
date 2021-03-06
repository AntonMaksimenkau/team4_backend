package com.team4.testingsystem.converters;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import com.team4.testingsystem.dto.ErrorReportDTO;
import com.team4.testingsystem.dto.ListeningTopicDTO;
import com.team4.testingsystem.dto.QuestionDTO;
import com.team4.testingsystem.dto.TestDTO;
import com.team4.testingsystem.dto.TestInfo;
import com.team4.testingsystem.entities.Answer;
import com.team4.testingsystem.entities.ChosenOption;
import com.team4.testingsystem.entities.ContentFile;
import com.team4.testingsystem.entities.ModuleGrade;
import com.team4.testingsystem.entities.Test;
import com.team4.testingsystem.enums.Modules;
import com.team4.testingsystem.exceptions.ContentFileNotFoundException;
import com.team4.testingsystem.services.AnswerService;
import com.team4.testingsystem.services.ChosenOptionService;
import com.team4.testingsystem.services.ContentFilesService;
import com.team4.testingsystem.services.ErrorReportsService;
import com.team4.testingsystem.services.ModuleGradesService;
import com.team4.testingsystem.services.QuestionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TestConverter {

    private final QuestionService questionService;
    private final ContentFilesService contentFilesService;
    private final ChosenOptionService chosenOptionService;
    private final ModuleGradesService moduleGradesService;
    private final AnswerService answerService;
    private final ErrorReportsService errorReportsService;

    public TestInfo convertToInfo(Test test) {
        Integer totalScore = moduleGradesService.getGradesByTest(test).values().stream()
                .map(ModuleGrade::getGrade)
                .reduce(0, Integer::sum);
        return new TestInfo(test, totalScore);
    }

    public TestDTO convertToDTO(Test test) {
        TestDTO testDTO = new TestDTO(test);
        attachQuestions(testDTO);
        attachContentFile(testDTO);
        attachEssay(testDTO);
        attachSpeaking(testDTO);
        attachErrorReports(testDTO);
        return testDTO;
    }

    private void attachQuestions(TestDTO testDTO) {
        Map<Long, Answer> chosenAnswerByQuestionId = chosenOptionService.getAllByTestId(testDTO.getId())
                .stream()
                .collect(toMap(option -> option.getId().getQuestion().getId(), ChosenOption::getAnswer));
        Map<String, List<QuestionDTO>> questions = questionService.getQuestionsByTestId(testDTO.getId()).stream()
                .peek(question -> Collections.shuffle(question.getAnswers(),
                        new Random(Objects.hash(question.getId(), testDTO.getId()))))
                .map(QuestionDTO::create)
                .peek(question -> checkChosenAnswer(question, chosenAnswerByQuestionId))
                .collect(groupingBy(QuestionDTO::getModule));
        testDTO.setQuestions(questions);
    }

    private void attachContentFile(TestDTO testDTO) {
        final ContentFile contentFile = testDTO.getQuestions()
                .getOrDefault(Modules.LISTENING.getName(), List.of()).stream()
                .map(QuestionDTO::getId)
                .map(contentFilesService::getContentFileByQuestionId)
                .findFirst()
                .orElseThrow(ContentFileNotFoundException::new);
        testDTO.setContentFile(new ListeningTopicDTO(contentFile));
    }

    private void attachEssay(TestDTO testDTO) {
        answerService.tryDownloadEssay(testDTO.getId())
                .ifPresent(testDTO::setEssayText);
    }

    private void attachSpeaking(TestDTO testDTO) {
        answerService.tryDownloadSpeaking(testDTO.getId())
                .ifPresent(testDTO::setSpeakingUrl);
    }

    private void attachErrorReports(TestDTO testDTO) {
        List<ErrorReportDTO> errorReports = errorReportsService.getReportsByTest(testDTO.getId()).stream()
                .map(ErrorReportDTO::new)
                .collect(Collectors.toList());
        testDTO.setErrorReports(errorReports);
    }

    private void checkChosenAnswer(QuestionDTO questionDTO, Map<Long, Answer> chosenAnswerByQuestionId) {
        if (!chosenAnswerByQuestionId.containsKey(questionDTO.getId())) {
            return;
        }
        Long chosenAnswerId = chosenAnswerByQuestionId.get(questionDTO.getId()).getId();
        questionDTO.getAnswers().stream()
                .filter(answer -> answer.getId().equals(chosenAnswerId))
                .forEach(answer -> answer.setChecked(true));
    }
}
