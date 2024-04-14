package edu.java.scrapper.dao.repository.jpa;

import edu.java.scrapper.dao.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaQuestionRepository extends JpaRepository<QuestionEntity, Long> {
}
