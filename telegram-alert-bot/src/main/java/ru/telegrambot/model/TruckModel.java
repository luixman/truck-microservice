package ru.telegrambot.model;

import lombok.*;
import ru.telegrambot.entity.Company;


@Data
public class TruckModel {

    long id;
    long uniqId;
    String name;
    String carNumber;
    int issueYear;
    String otherInformation;
    Company company;

}
