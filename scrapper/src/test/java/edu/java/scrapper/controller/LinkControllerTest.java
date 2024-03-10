package edu.java.scrapper.controller;

import edu.java.scrapper.dto.request.AddLinkRequest;
import edu.java.scrapper.dto.request.RemoveLinkRequest;
import edu.java.scrapper.dto.response.LinkResponse;
import edu.java.scrapper.dto.response.ListLinksResponse;
import edu.java.scrapper.exception.ChatIsNotRegisteredException;
import edu.java.scrapper.exception.LinkIsNotTrackedException;
import edu.java.scrapper.exception.ReAddLinkException;
import edu.java.scrapper.exception.ReRegistrationException;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LinkControllerTest {
    @Autowired
    private LinkController linkController;
    @Autowired
    private ChatController chatController;

    @Test
    void shouldThrowChatIsNotRegisteredExceptionForGet() {
        Assertions.assertThrows(ChatIsNotRegisteredException.class, () -> linkController.getAllLinks(1L));
    }

    @Test
    void shouldReturnListLinksResponseForGet() throws ReRegistrationException, ChatIsNotRegisteredException {
        ResponseEntity<ListLinksResponse> expected =
            new ResponseEntity<>(new ListLinksResponse(new ArrayList<>(), 0), HttpStatus.OK);

        chatController.addChat(2L);
        ResponseEntity<ListLinksResponse> actual = linkController.getAllLinks(2L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldThrowChatIsNotRegisteredExceptionForPost() {
        AddLinkRequest addLinkRequest = new AddLinkRequest("https://github.com/IA1I/java-course-2023-backend");

        Assertions.assertThrows(ChatIsNotRegisteredException.class, () -> linkController.addLink(3L, addLinkRequest));
    }

    @Test
    void shouldReturnLinkResponseForPost()
        throws ReRegistrationException, ChatIsNotRegisteredException, ReAddLinkException {
        ResponseEntity<LinkResponse> expected = new ResponseEntity<>(
            new LinkResponse(0, "https://github.com/IA1I/java-course-2023-backend"),
            HttpStatus.OK
        );
        AddLinkRequest addLinkRequest = new AddLinkRequest("https://github.com/IA1I/java-course-2023-backend");

        chatController.addChat(4L);
        ResponseEntity<LinkResponse> actual = linkController.addLink(4L, addLinkRequest);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldThrowReAddLinkExceptionForPost()
        throws ReRegistrationException, ChatIsNotRegisteredException, ReAddLinkException {
        AddLinkRequest addLinkRequest = new AddLinkRequest("https://github.com/IA1I/java-course-2023-backend");

        chatController.addChat(5L);
        linkController.addLink(5L, addLinkRequest);

        Assertions.assertThrows(ReAddLinkException.class, () -> linkController.addLink(5L, addLinkRequest));
    }

    @Test
    void shouldThrowChatIsNotRegisteredExceptionForDelete() {
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest("https://github.com/IA1I/java-course-2023-backend");

        Assertions.assertThrows(
            ChatIsNotRegisteredException.class,
            () -> linkController.deleteLink(6L, removeLinkRequest)
        );
    }

    @Test
    void shouldThrowLinkIsNotTrackedExceptionForDelete() throws ReRegistrationException {
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest("https://github.com/IA1I/java-course-2023-backend");

        chatController.addChat(7L);

        Assertions.assertThrows(
            LinkIsNotTrackedException.class,
            () -> linkController.deleteLink(7L, removeLinkRequest)
        );
    }

    @Test
    void shouldThrowForDelete()
        throws ReRegistrationException, ChatIsNotRegisteredException, ReAddLinkException, LinkIsNotTrackedException {
        ResponseEntity<LinkResponse> expected = new ResponseEntity<>(
            new LinkResponse(0, "https://github.com/IA1I/java-course-2023-backend"),
            HttpStatus.OK
        );
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest("https://github.com/IA1I/java-course-2023-backend");
        AddLinkRequest addLinkRequest = new AddLinkRequest("https://github.com/IA1I/java-course-2023-backend");

        chatController.addChat(8L);
        linkController.addLink(8L, addLinkRequest);

        ResponseEntity<LinkResponse> actual = linkController.deleteLink(8L, removeLinkRequest);

        assertThat(actual).isEqualTo(expected);
    }
}
