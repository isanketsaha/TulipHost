insert into user_group ( created_date,group_name,modified_date) VALUES (NOW(), 'UG_TEACHER' ,NOW());
insert into user_group ( created_date,group_name,modified_date) VALUES (NOW(), 'UG_STAFF' ,NOW());
insert into user_group ( created_date,group_name,modified_date) VALUES (NOW(), 'UG_PRINCIPAL' ,NOW());
insert into user_group ( created_date,group_name,modified_date) VALUES (NOW(), 'UG_ADMIN' ,NOW());

INSERT INTO employee(created_date, name ,phone_number,modified_date,gender,group_id,active,locked)
VALUES (NOW(),'Sanket Saha','9563838313',NOW(),'MALE',(select pk from user_group where group_name = 'UG_ADMIN'),true,false);

insert into credential(created_date,user_name,password,modified_date, fk_employee,reset_password)
VALUES (NOW(),'isanket','$2a$10$feltrGKNAxOKG6nMlSZLO..4Dlh7bCEOb9k7raEw9TlyV9dPkHBly',NOW(),(select pk from employee where name = 'Sanket Saha'),true);

insert into class_details(created_date,std) values (NOW(),'PLAY');
insert into class_details(created_date, std) values (NOW(),'NURSERY');
insert into class_details(created_date, std) values (NOW(),'KG');
insert into class_details(created_date, std) values (NOW(),'FIRST');