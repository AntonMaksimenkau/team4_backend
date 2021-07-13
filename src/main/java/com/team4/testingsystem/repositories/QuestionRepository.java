package com.team4.testingsystem.repositories;

import com.team4.testingsystem.entities.Question;
import com.team4.testingsystem.entities.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends CrudRepository<Question, Long> {
    Optional<Question> findById(Long id);
    Question save(Question question);
}
