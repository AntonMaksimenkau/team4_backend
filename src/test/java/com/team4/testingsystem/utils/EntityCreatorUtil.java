package com.team4.testingsystem.utils;

import com.team4.testingsystem.dto.ContentFileRequest;
import com.team4.testingsystem.dto.QuestionDTO;
import com.team4.testingsystem.entities.Level;
import com.team4.testingsystem.entities.Module;
import com.team4.testingsystem.entities.Question;
import com.team4.testingsystem.entities.Test;
import com.team4.testingsystem.entities.User;
import com.team4.testingsystem.entities.UserRole;
import com.team4.testingsystem.enums.Status;

import java.time.LocalDateTime;

public class EntityCreatorUtil {
    public static Question createQuestion() {
        return new Question.Builder()
                .id(1L)
                .body("some text")
                .module(createModule())
                .level(createLevel())
                .creator(createUser())
                .isAvailable(true)
                .build();
    }

    public static QuestionDTO createQuestionDto() {
        return new QuestionDTO.Builder()
                .body("some text")
                .module("new module")
                .level("new level")
                .creator("name")
                .isAvailable(true)
                .build();
    }

    public static User createUser() {
        User user = new User();
        UserRole userRole = new UserRole();
        userRole.setId(1);
        userRole.setRoleName("role");
        user.setId(1L);
        user.setName("name");
        user.setLanguage("en");
        user.setLogin("login");
        user.setPassword("password");
        user.setRole(userRole);
        return user;
    }

    public static Module createModule() {
        Module module = new Module();
        module.setId(1L);
        module.setName("new module");
        return module;
    }

    public static Level createLevel() {
        Level level = new Level();
        level.setId(1L);
        level.setName("new level");
        return level;
    }

    public static ContentFileRequest createContentFileRequest(Long questionId, String url) {
        ContentFileRequest cfr = new ContentFileRequest();
        cfr.setQuestionId(questionId);
        cfr.setUrl(url);
        return cfr;
    }

    public static Test createTest(User user) {
        return Test.builder()
                .user(user)
                .status(Status.STARTED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Question createQuestion(User user) {
        return new Question.Builder()
                .body("some text")
                .module(createModule())
                .level(createLevel())
                .creator(user)
                .isAvailable(true)
                .build();
    }
}
