package ru.telegrambot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.telegrambot.entity.Company;


public interface CompanyRepo extends JpaRepository<Company,Long> {

}
