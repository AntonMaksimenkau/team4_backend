package com.team4.testingsystem.repositories;

import com.team4.testingsystem.entities.ContentFile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ContentFilesRepository extends CrudRepository<ContentFile, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE ContentFile cf SET cf.url = ?1 WHERE cf.id = ?2")
    int changeUrl(String url, Long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE from ContentFile cf WHERE cf.id = ?1")
    int removeById(Long id);

    @Query("SELECT cf from ContentFile cf " +
           "JOIN cf.questions q " +
           "JOIN q.level l " +
           "WHERE l.name = ?1 " +
           "order by function('random') ")
    List<ContentFile> getRandomFiles(String level, Pageable pageable);

}
