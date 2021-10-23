package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class MetaEngineDTO {
    private final UIGameEntry defaultEntry;
    private final Set<UIGameEntry> configs;
    private final UIGameEntry selectedEntry;
}
