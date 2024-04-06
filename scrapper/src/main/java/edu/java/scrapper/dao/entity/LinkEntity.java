package edu.java.scrapper.dao.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "link")
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "link_id")
    private Long linkId;

    @Column(unique = true)
    private String uri;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "last_check")
    private OffsetDateTime lastCheck;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "tracked_link",
               joinColumns = {@JoinColumn(name = "link_id", referencedColumnName = "link_id")},
               inverseJoinColumns = {@JoinColumn(name = "chat_id", referencedColumnName = "id")})
    private Set<ChatEntity> chats = new HashSet<>();

    @OneToOne(mappedBy = "link", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private QuestionEntity question;
}
