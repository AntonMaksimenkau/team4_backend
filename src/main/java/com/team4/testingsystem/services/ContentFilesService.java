package com.team4.testingsystem.services;

import com.team4.testingsystem.entities.ContentFile;
import com.team4.testingsystem.entities.Question;

import java.util.List;

public interface ContentFilesService {

    Iterable<ContentFile> getAll();

    ContentFile getById(long id);

    ContentFile add(String url, List<Question> questions);

    ContentFile update(Long id, String url, List<Question> questions);

    ContentFile updateQuestions(Long id, List<Question> questions);

    void updateURL(Long id, String newUrl);

    void removeById(Long id);

    ContentFile getRandomContentFile(String level);

    ContentFile getContentFileByQuestionId(Long id);
}
