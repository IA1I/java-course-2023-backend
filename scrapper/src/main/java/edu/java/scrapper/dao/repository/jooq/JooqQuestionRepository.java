package edu.java.scrapper.dao.repository.jooq;

import edu.java.scrapper.dto.Question;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import static edu.java.scrapper.dao.jooq.Tables.QUESTION;

@Repository
public class JooqQuestionRepository {
    private final DSLContext dslContext;

    @Autowired
    public JooqQuestionRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Question get(long linkId) {
        return dslContext.select(QUESTION.fields())
            .from(QUESTION)
            .where(QUESTION.LINK_ID.eq(linkId))
            .fetchOneInto(Question.class);
    }

    public void save(Question question) {
        dslContext.insertInto(QUESTION)
            .set(QUESTION.LINK_ID, question.getLinkId())
            .set(QUESTION.COMMENTS_COUNT, question.getCommentsCount())
            .set(QUESTION.ANSWERS_COUNT, question.getAnswersCount())
            .execute();
    }

    public void update(Question question) {
        dslContext.update(QUESTION)
            .set(QUESTION.COMMENTS_COUNT, question.getCommentsCount())
            .set(QUESTION.ANSWERS_COUNT, question.getAnswersCount())
            .where(QUESTION.LINK_ID.eq(question.getLinkId()))
            .execute();
    }
}
