package com.team4.testingsystem.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "content_file")
public class ContentFile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "question_content_file",
            joinColumns = @JoinColumn(name = "content_file_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    List<Question> questions = new ArrayList<>();

    @Column(name = "url")
    private String url;

    @Column(name = "topic")
    private String topic;

    @Column(name = "is_available")
    private boolean available = true;

    public ContentFile() {
    }

    public ContentFile(String url) {
        this.url = url;
    }

    public ContentFile(String url, String topic, List<Question> questions) {
        this.questions = questions;
        this.url = url;
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContentFile that = (ContentFile) o;
        return available == that.available
                && Objects.equals(id, that.id)
                && Objects.equals(questions, that.questions)
                && Objects.equals(url, that.url)
                && Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, questions, url, topic, available);
    }
}
