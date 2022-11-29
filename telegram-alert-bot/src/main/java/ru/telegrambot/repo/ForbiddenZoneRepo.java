package ru.telegrambot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.telegrambot.entity.ForbiddenZone;


public interface ForbiddenZoneRepo extends JpaRepository<ForbiddenZone,Long> {

}
