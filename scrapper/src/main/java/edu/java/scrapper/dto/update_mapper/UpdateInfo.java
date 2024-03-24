package edu.java.scrapper.dto.update_mapper;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateInfo {
    protected OffsetDateTime updatedAt;
    protected String description;
}
