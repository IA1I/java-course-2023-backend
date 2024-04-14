package edu.java.scrapper.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "question")
public class QuestionEntity {
    @Id
    @Column(name = "link_id")
    private Long linkId;

    @Column(name = "comments_count")
    private Integer commentsCount;

    @Column(name = "answers_count")
    private Integer answersCount;

    @OneToOne
    @MapsId
    @JoinColumn(name = "link_id")
    private LinkEntity link;
}
