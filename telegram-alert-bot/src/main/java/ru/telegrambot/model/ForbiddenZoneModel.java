package ru.telegrambot.model;

import lombok.*;


@Data
public class ForbiddenZoneModel {
    Long id;
    String zoneName;
    Boolean deactivated;
}
