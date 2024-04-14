package edu.java.scrapper.dao.repository.jpa;

import edu.java.scrapper.dao.entity.LinkEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinkRepository extends JpaRepository<LinkEntity, Long> {
    Optional<LinkEntity> findByUri(String uri);

    Boolean existsByUri(String uri);

//    void updateByLinkId(LinkEntity entity);

    List<LinkEntity> findAllByLastCheckLessThan(OffsetDateTime offsetDateTime);

    @Query(value = "SELECT DISTINCT l.* FROM tracked_link tl LEFT JOIN link l ON tl.link_id = l.link_id",
           nativeQuery = true)
    List<LinkEntity> findDistinctTrackedLinks();
}
