package edu.java.scrapper.linkchecker;

import edu.java.scrapper.dto.Link;
import edu.java.scrapper.dto.update_mapper.UpdateInfo;

abstract public class LinkChecker {
    abstract public UpdateInfo getUpdateInformation(Link link);

    abstract public void update();
}
