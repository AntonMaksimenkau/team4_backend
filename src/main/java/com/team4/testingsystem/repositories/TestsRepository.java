package com.team4.testingsystem.repositories;

import com.team4.testingsystem.entities.Test;
import com.team4.testingsystem.entities.User;
import com.team4.testingsystem.enums.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestsRepository extends CrudRepository<Test, Long> {

    @Query(value = "select t from Test t where t.id = ?1 and t.isAvailable = true")
    Optional<Test> findById(Long id);

    @Query(value = "select t from Test t "
            + "where t.user.id = ?1 "
            + "and t.isAvailable = true "
            + "and t.status = 'ASSIGNED' ")
    Optional<Test> getAssignedTestByUserId(Long id);

    @Query(value = "select t from Test t where t.user.id = ?1 and t.isAvailable = true "
            + "order by case "
            + "when t.status = 'STARTED' then 'A' "
            + "when t.status = 'ASSIGNED' then 'B' ELSE 'C' end, "
            + "t.verifiedAt desc nulls last, "
            + "t.deadline desc nulls last, "
            + "t.assignedAt desc nulls last ")
    List<Test> getAllByUserId(Long userId, Pageable pageable);

    @Query(value = "select t from Test t where t.user.id = ?1 "
            + "and t.isAvailable = true "
            + "and t.level.name = ?2 order by case "
            + "when t.status = 'STARTED' then 'A' "
            + "when t.status = 'ASSIGNED' then 'B' ELSE 'C' end, "
            + "t.verifiedAt desc nulls last, "
            + "t.deadline desc nulls last, "
            + "t.assignedAt desc nulls last, "
            + "t.completedAt asc nulls last, "
            + "t.id desc nulls last")
    List<Test> getAllByUserAndLevel(Long userId, String level, Pageable pageable);

    @Query(value = "select t from Test t where t.status in ?1 and t.isAvailable = true "
            + "order by case "
            + "when t.priority = 'High' then 'A' "
            + "when t.priority = 'Medium' then 'B' "
            + "when t.priority = 'Low' then 'C' ELSE 'D' end, "
            + "t.deadline asc nulls last, "
            + "t.assignedAt desc nulls last, "
            + "t.completedAt asc nulls last, "
            + "t.id desc nulls last")
    List<Test> getByStatuses(Status[] statuses, Pageable pageable);

    @Query(value = "select t from Test t "
            + "where t.status in ?1 and t.isAvailable = true and t.user.id <> ?2 "
            + "order by case "
            + "when t.priority = 'High' then 'A' "
            + "when t.priority = 'Medium' then 'B' "
            + "when t.priority = 'Low' then 'C' ELSE 'D' end, "
            + "t.deadline asc nulls last, "
            + "t.assignedAt desc nulls last, "
            + "t.completedAt asc nulls last, "
            + "t.id desc nulls last")
    List<Test> getByStatusesExcludingUser(Status[] statuses, Long userId, Pageable pageable);

    @Query(value = "select t from Test t "
            + "where t.user = ?1 "
            + "and t.isAvailable = true "
            + "and t.assignedAt is null "
            + "and t.startedAt >= ?2 ")
    List<Test> getSelfStartedByUserAfter(User user, Instant date);

    @Query(value = "select t from Test t where t.coach.id = ?1 and t.status in ?2 and t.isAvailable = true "
            + "order by case "
            + "when t.priority = 'High' then 'A' "
            + "when t.priority = 'Medium' then 'B' "
            + "when t.priority = 'Low' then 'C' ELSE 'D' end, "
            + "t.deadline asc nulls last, "
            + "t.assignedAt desc nulls last, "
            + "t.completedAt asc nulls last, "
            + "t.id desc nulls last")
    List<Test> getAllByAssignedCoachAndStatuses(Long coachId, Status[] status, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "update Test t set t.status = ?2 where t.id = ?1 and t.isAvailable = true")
    int updateStatusByTestId(Long testId, Status newStatus);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Test t SET t.assignedAt = null, t.deadline = null "
            + "WHERE t.id = ?1 and t.isAvailable = true")
    int deassign(Long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Test t SET t.completedAt = ?1, t.status = 'COMPLETED' "
            + "where t.id = ?2 and t.isAvailable = true")
    int finish(Instant finishDate, Long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Test t SET t.verifiedAt = ?1, t.status = 'VERIFIED' "
            + "where t.id = ?2 and t.isAvailable = true")
    int coachSubmit(Instant updateDate, Long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Test t SET t.isAvailable = false where t.id = ?1 ")
    int archiveById(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Test t SET t.coach = ?1 where t.id = ?2 and t.isAvailable = true")
    int assignCoach(User coach, Long id);


    @Transactional
    @Modifying
    @Query(value = "UPDATE Test t SET t.coach = null, t.status = 'COMPLETED' "
            + "where t.id = ?1 and t.isAvailable = true ")
    int deassignCoach(Long id);

    @Query(value = " select case when count(t)> 0 then true else false end "
            + "from Test t where t.user = ?1 "
            + "and t.isAvailable = true "
            + "and t.assignedAt is not null "
            + "and t.status = 'ASSIGNED' ")
    boolean hasAssignedTests(User user);

    @Query(value = " select case when count(t) > 0 then true else false end "
            + "from Test t where t.user.id = ?1 "
            + "and t.status = 'STARTED' "
            + "and t.isAvailable = true ")
    boolean hasStartedTests(Long userId);

    @Transactional
    @Modifying
    @Query(value = "update Test t set t.listeningAttempts = ?1 where t.id = ?2 and t.listeningAttempts = ?1 + 1 ")
    int updateListeningAttempts(Integer attempts, Long testId);

}
