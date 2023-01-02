insert into user_group ( created_date,authority,last_modified_date) VALUES (NOW(), 'UG_TEACHER' ,NOW());

insert into user_group ( created_date,authority,last_modified_date) VALUES (NOW(), 'UG_STAFF' ,NOW());
insert into user_group ( created_date,authority,last_modified_date) VALUES (NOW(), 'UG_PRINCIPAL' ,NOW());
insert into user_group ( created_date,authority,last_modified_date) VALUES (NOW(), 'UG_ADMIN' ,NOW());



INSERT INTO employee(created_date, name ,phone_number,last_modified_date,gender,group_id,active,locked, credential_id)
VALUES (NOW(),'Teacher','8476398172',NOW(),'MALE',(select id from user_group where authority = 'UG_TEACHER'),true,false,1 );
INSERT INTO employee(created_date, name ,phone_number,last_modified_date,gender,group_id,active,locked, credential_id)
 VALUES (NOW(),'Test Staff','8476326572',NOW(),'FEMALE',(select id from user_group where authority = 'UG_STAFF'),true,false,2 );
INSERT INTO employee(created_date, name ,phone_number,last_modified_date,gender,group_id,active,locked, credential_id)
VALUES (NOW(),'Test Principal','8476326172',NOW(),'FEMALE',(select id from user_group where authority = 'UG_PRINCIPAL'),true,false,3);
INSERT INTO employee(created_date, name ,phone_number,last_modified_date,gender,group_id,active,locked, credential_id)
VALUES (NOW(),'Sanket Saha','9563838313',NOW(),'MALE',(select id from user_group where authority = 'UG_ADMIN'),true,false,4);

insert into class_details(created_date,std,last_modified_date,session_id) values (NOW(),'PLAY',NOW(),1);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'NURSERY',NOW(),1);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'KG',NOW(),1);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'1',NOW(),1);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'2',NOW(),1);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'3',NOW(),1);

insert into session ( created_date,display_text,from_date,to_date,last_modified_date) VALUES (NOW(), '2022-2023','2023-04-01','2022-03-31' ,NOW());

insert into credential(created_date,user_name,password,last_modified_date,reset_password)
VALUES (NOW(),'istaff','$2a$10$goT8e664ahxUn5bFYBqktOt6Ve0j5BO4PWOjmLVmqwsB4J8DQniCe',NOW(),true);
insert into credential(created_date,user_name,password,last_modified_date,reset_password)
VALUES (NOW(),'itulip','$2a$10$goT8e664ahxUn5bFYBqktOt6Ve0j5BO4PWOjmLVmqwsB4J8DQniCe',NOW(),true);
insert into credential(created_date,user_name,password,last_modified_date,reset_password)
VALUES (NOW(),'iteacher','$2a$10$goT8e664ahxUn5bFYBqktOt6Ve0j5BO4PWOjmLVmqwsB4J8DQniCe',NOW(),true);
insert into credential(created_date,user_name,password,last_modified_date,reset_password)
VALUES (NOW(),'isanket','$2a$10$goT8e664ahxUn5bFYBqktOt6Ve0j5BO4PWOjmLVmqwsB4J8DQniCe',NOW(),true);
