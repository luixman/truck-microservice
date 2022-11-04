
CREATE TABLE truck(
    id SERIAL PRIMARY KEY,
    uid bigint,  --Уникальный ид, с которым приходят сообщения
    name varchar(50),
    car_number varchar(12),
    issue_year smallint,
    other_information varchar,
    company_id bigint,
    CONSTRAINT fk_company
                  FOREIGN KEY (company_id)
                  REFERENCES company(id)
                  ON DELETE SET NULL
);

INSERT INTO truck(uid, name, car_number, issue_year, other_information, company_id) values
                                                             (100001,'ГАЗель NEXT','C423CO197',2017,'лизинг',1),
                                                             (100002,'Volkswagen transporter','В444ОР777',2014,'юр. запреты',2),
                                                             (100003,'ГАЗ 3302','О979Т06',2005,'',2)