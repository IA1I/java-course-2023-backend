package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcTrackedLinkRepository;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.dto.request.LinkUpdateRequest;
import edu.java.scrapper.dto.update_mapper.UpdateInfo;
import edu.java.scrapper.linkchecker.LinkChecker;
import edu.java.scrapper.service.LinkUpdater;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Log4j2
public class JdbcLinksUpdaterService implements LinkUpdater {
    private static final long CHECK_TIME = 3L;
    private final JdbcLinkRepository linkRepository;
    private final JdbcTrackedLinkRepository trackedLinkRepository;
    private final BotClient botClient;
    public final Map<String, LinkChecker> checkerMap;

    public JdbcLinksUpdaterService(
        JdbcLinkRepository linkRepository,
        JdbcTrackedLinkRepository trackedLinkRepository,
        BotClient botClient,
        Map<String, LinkChecker> checkerMap
    ) {
        this.linkRepository = linkRepository;
        this.trackedLinkRepository = trackedLinkRepository;
        this.botClient = botClient;
        this.checkerMap = checkerMap;
    }

    @Override
    @Transactional
    public int update() {
        int updatesCount = 0;
//        List<Link> links = getLinksToCheck();
        List<Link> links = linkRepository.getLinksToCheck();
        log.info("Get links {}", links);

        for (Link link : links) {
            LinkChecker linkChecker = checkerMap.get(link.getUri().getHost());
            UpdateInfo updateInfo = linkChecker.getUpdateInformation(link);

            if (updateInfo.getUpdatedAt().isAfter(link.getLastCheck())) {
                LinkUpdateRequest linkUpdateRequest = getLinkUpdateRequest(link, updateInfo);
                Mono<String> sentUpdate = botClient.sendUpdate(linkUpdateRequest);
                sentUpdate.block();
                log.info("Send update {} to bot", linkUpdateRequest);

                updatesCount++;
            }

            link.setLastCheck(OffsetDateTime.now(ZoneId.systemDefault()));
            linkRepository.update(link);
        }

        return updatesCount;
    }

    private LinkUpdateRequest getLinkUpdateRequest(Link link, UpdateInfo updateInfo) {
        long id = link.getLinkId();
        String url = link.getUri().toString();
        Long[] tgChatIds = getTgChatsIdForUpdate(link);

        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(id, url, updateInfo.getDescription(), tgChatIds);
        return linkUpdateRequest;
    }

    private Long[] getTgChatsIdForUpdate(Link link) {
        List<Chat> chats = trackedLinkRepository.getAllChatsByLinkId(link.getLinkId());
        Long[] tgChatIds = new Long[chats.size()];
        for (int i = 0; i < chats.size(); i++) {
            tgChatIds[i] = chats.get(i).getTgChatId();
        }

        return tgChatIds;
    }

    private List<Link> getLinksToCheck() {
        OffsetDateTime now = OffsetDateTime.now();
        Predicate<Link> filter = link -> Duration.between(link.getLastCheck(), now).toHours() > CHECK_TIME;

        return linkRepository.getAll().stream()
            .filter(filter)
            .toList();
    }
}
