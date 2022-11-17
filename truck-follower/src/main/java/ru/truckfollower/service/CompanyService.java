package ru.truckfollower.service;


import org.springframework.stereotype.Service;
import ru.truckfollower.entity.Company;
import ru.truckfollower.repo.CompanyRepo;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepo companyRepo;
    public CompanyService(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }


    public List<Company> getAll(){
        return companyRepo.findAll();
    }
}
