
insert into user_group ( created_date,group_name,last_modified_date) VALUES (NOW(), 'UG_TEACHER' ,NOW());

insert into user_group ( created_date,group_name,last_modified_date) VALUES (NOW(), 'UG_STAFF' ,NOW());
insert into user_group ( created_date,group_name,last_modified_date) VALUES (NOW(), 'UG_PRINCIPAL' ,NOW());
insert into user_group ( created_date,group_name,last_modified_date) VALUES (NOW(), 'UG_ADMIN' ,NOW());

INSERT INTO employee(created_date, name ,phone_number,last_modified_date,gender,group_id,active,locked)
VALUES (NOW(),'Teacher','8476398172',NOW(),'MALE',(select id from user_group where group_name = 'UG_TEACHER'),true,false );
INSERT INTO employee(created_date, name ,phone_number,last_modified_date,gender,group_id,active,locked)
 VALUES (NOW(),'Test Staff','8476326572',NOW(),'FEMALE',(select id from user_group where group_name = 'UG_STAFF'),true,false );
INSERT INTO employee(created_date, name ,phone_number,last_modified_date,gender,group_id,active,locked)
VALUES (NOW(),'Test Principal','8476326172',NOW(),'FEMALE',(select id from user_group where group_name = 'UG_PRINCIPAL'),true,false);
INSERT INTO employee(created_date, name ,phone_number,last_modified_date,gender,group_id,active,locked)
VALUES (NOW(),'Sanket Saha','9563838313',NOW(),'MALE',(select id from user_group where group_name = 'UG_ADMIN'),true,false);


insert into credential(created_date,user_name,password,last_modified_date, fk_employee,reset_password)
VALUES (NOW(),'istaff','$2a$10$feltrGKNAxOKG6nMlSZLO..4Dlh7bCEOb9k7raEw9TlyV9didHBly',NOW(),(select id from employee where name = 'Test Staff'),true);
insert into credential(created_date,user_name,password,last_modified_date, fk_employee,reset_password)
VALUES (NOW(),'itulip','$2a$10$feltrGKNAxOKG6nMlSZLO..4Dlh7bCEOb9k7raEw9TlyV9didHBly',NOW(),(select id from employee where name = 'Test Principal'),true);
insert into credential(created_date,user_name,password,last_modified_date, fk_employee,reset_password)
VALUES (NOW(),'iteacher','$2a$10$feltrGKNAxOKG6nMlSZLO..4Dlh7bCEOb9k7raEw9TlyV9didHBly',NOW(),(select id from employee where name = 'Teacher'),true);
insert into credential(created_date,user_name,password,last_modified_date, fk_employee,reset_password)
VALUES (NOW(),'isanket','$2a$10$feltrGKNAxOKG6nMlSZLO..4Dlh7bCEOb9k7raEw9TlyV9didHBly',NOW(),(select id from employee where name = 'Sanket Saha'),true);


insert into class_details(created_date,std) values (NOW(),'PLAY');
insert into class_details(created_date, std) values (NOW(),'NURSERY');
insert into class_details(created_date, std) values (NOW(),'KG');
insert into class_details(created_date, std) values (NOW(),'FIRST');
