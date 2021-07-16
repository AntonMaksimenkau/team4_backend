package com.team4.testingsystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team4.testingsystem.enums.Status;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "test")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime  createdAt;

    @Column(name = "updated_at")
    private LocalDateTime  updatedAt;

    @Column(name = "started_at")
    private LocalDateTime  startedAt;

    @Column(name = "finished_at")
    private LocalDateTime  finishedAt;


    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "evaluation")
    private int evaluation;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public LocalDateTime  getUpdatedAt() {
        return updatedAt;
    }


    public LocalDateTime  getStartedAt() {
        return startedAt;
    }

    public LocalDateTime  getFinishedAt() {
        return finishedAt;
    }


    public Status getStatus() {
        return status;
    }


    public int getEvaluation() {
        return evaluation;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
    }

    public static class Builder {

        private Test test;

        public Builder() {
            this.test = new Test();
        }

        public Builder user(User user) {
            test.user = user;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            test.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            test.updatedAt = updatedAt;
            return this;
        }

        public Builder startedAt(LocalDateTime startedAt) {
            test.startedAt = startedAt;
            return this;
        }

        public Builder finishedAt(LocalDateTime finishedAt) {
            test.finishedAt = finishedAt;
            return this;
        }

        public Builder status(Status status) {
            test.status = status;
            return this;
        }

        public Builder evaluation(int evaluation) {
            test.evaluation = evaluation;
            return this;
        }

        public Test build() {
            return test;
        }


    }
}
