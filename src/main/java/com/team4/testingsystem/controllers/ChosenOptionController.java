package com.team4.testingsystem.controllers;

import com.team4.testingsystem.entities.ChosenOption;
import com.team4.testingsystem.entities.Test;
import com.team4.testingsystem.entities.TestQuestionID;
import com.team4.testingsystem.services.ChosenOptionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/chosen_option")
public class ChosenOptionController {

    private ChosenOptionService chosenOptionService;

    @Autowired
    public ChosenOptionController(ChosenOptionService chosenOptionService) {
        this.chosenOptionService = chosenOptionService;
    }

    @ApiOperation(value = "Get a single chosen option by TestQuestionID")
    @GetMapping(path = "/")
    public ChosenOption getById(@RequestBody TestQuestionID testQuestionID) {
        return chosenOptionService.getById(testQuestionID);
    }

    @ApiOperation(value = "Use it to get all chosen options from the database")
    @GetMapping(path = "/options")
    public Iterable<ChosenOption> getAllByTest(@RequestBody Test test) {
        return chosenOptionService.getChosenOptionByTest(test);
    }

    @ApiOperation(value = "Use it to add a chosen option")
    @PostMapping(path = "/")
    public void save(@RequestBody ChosenOption chosenOption) {
        chosenOptionService.save(chosenOption);
    }
}
