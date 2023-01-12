insert into user_group ( created_date,authority,last_modified_date) VALUES (NOW(), 'UG_TEACHER' ,NOW());

insert into user_group ( created_date,authority,last_modified_date) VALUES (NOW(), 'UG_STAFF' ,NOW());
insert into user_group ( created_date,authority,last_modified_date) VALUES (NOW(), 'UG_PRINCIPAL' ,NOW());
insert into user_group ( created_date,authority,last_modified_date) VALUES (NOW(), 'UG_ADMIN' ,NOW());

insert into session ( created_date,display_text,from_date,to_date,last_modified_date) VALUES (NOW(), '2023-2024','2023-04-01','2024-03-31' ,NOW());
insert into session ( created_date,display_text,from_date,to_date,last_modified_date) VALUES (NOW(), '2022-2023','2022-04-01','2023-03-31' ,NOW());

insert into class_details(created_date,std,last_modified_date,session_id) values (NOW(),'LKG',NOW(),1);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'NURSERY',NOW(),1);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'UKG',NOW(),1);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'I',NOW(),1);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'II',NOW(),1);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'III',NOW(),1);

insert into class_details(created_date,std,last_modified_date,session_id) values (NOW(),'LKG',NOW(),2);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'NURSERY',NOW(),2);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'UKG',NOW(),2);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'I',NOW(),2);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'II',NOW(),2);
insert into class_details(created_date, std,last_modified_date,session_id) values (NOW(),'III',NOW(),2);



insert into credential(created_date,user_name,password,last_modified_date)
VALUES (NOW(),'isanket','$2a$10$goT8e664ahxUn5bFYBqktOt6Ve0j5BO4PWOjmLVmqwsB4J8DQniCe',NOW());


INSERT INTO employee(created_date, name ,phone_number,last_modified_date,gender,group_id,active,locked, credential_id,reset_credential)
VALUES (NOW(),'Sanket Saha','9563838313',NOW(),'MALE',(select id from user_group where authority = 'UG_ADMIN'),true,false,1, false);




INSERT INTO fees_catalog (fees_name, price, description, applicable_rule, std_id, active, created_by, last_modified_by, created_date, last_modified_date)
VALUES('Tution Fees', 700, 'Monthly fees for Student', 'Monthly', (select id from class_details where std='NURSERY' and session_id=1), 0, 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO fees_catalog (fees_name, price, description, applicable_rule , std_id, active, created_by, last_modified_by, created_date, last_modified_date)
VALUES('Tution Fees', 900, 'Monthly fees for Student', 'Monthly', (select id from class_details where std='UKG' and session_id=1), 0, 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO fees_catalog (fees_name, price, description, applicable_rule, std_id, active, created_by, last_modified_by, created_date, last_modified_date)
VALUES('Tution Fees', 1200, 'Monthly fees for Student', 'Monthly', (select id from class_details where std='I' and session_id=1), 0, 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO fees_catalog (fees_name, price, description, applicable_rule, std_id, active, created_by, last_modified_by, created_date, last_modified_date)
VALUES('Tution Fees', 1200, 'Monthly fees for Student', 'Monthly', (select id from class_details where std='II' and session_id=1), 0, 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);



INSERT INTO fees_catalog (fees_name, price, description, applicable_rule, std_id, active, created_by, last_modified_by, created_date, last_modified_date)
VALUES('Session Charge', 3000, 'Session fees for Student', 'Yearly', (select id from class_details where std='NURSERY' and session_id=1), 0, 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO fees_catalog (fees_name, price, description, applicable_rule, std_id, active, created_by, last_modified_by, created_date, last_modified_date)
VALUES('Session Charge', 3000, 'Session fees for Student', 'Yearly', (select id from class_details where std='UKG' and session_id=1), 0, 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO fees_catalog (fees_name, price, description, applicable_rule, std_id, active, created_by, last_modified_by, created_date, last_modified_date)
VALUES('Session Charge', 3000, 'Session fees for Student', 'Yearly', (select id from class_details where std='I' and session_id=1), 0, 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO fees_catalog (fees_name, price, description, applicable_rule, std_id, active, created_by, last_modified_by, created_date, last_modified_date)
VALUES('Session Charge', 4000, 'Session fees for Student', 'Yearly', (select id from class_details where std='II' and session_id=1), 0, 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


INSERT INTO fees_catalog (fees_name, price, description, applicable_rule, std_id, active, created_by, last_modified_by, created_date, last_modified_date)
VALUES('Late Fine', 50, 'Late Fine for Student', 'Monthly', (select id from class_details where std='NURSERY' and session_id=1), 0, 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO fees_catalog (fees_name, price, description, applicable_rule, std_id, active, created_by, last_modified_by, created_date, last_modified_date)
VALUES('Late Fine', 50, 'Late Fine for Student', 'Monthly', (select id from class_details where std='UKG' and session_id=1), 0, 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO fees_catalog (fees_name, price, description, applicable_rule, std_id, active, created_by, last_modified_by, created_date, last_modified_date)
VALUES('Late Fine', 50, 'Late Fine for Student', 'Monthly', (select id from class_details where std='I' and session_id=1), 0, 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO fees_catalog (fees_name, price, description, applicable_rule, std_id, active, created_by, last_modified_by, created_date, last_modified_date)
VALUES('Late Fine', 50, 'Late Fine for Student', 'Monthly', (select id from class_details where std='II' and session_id=1), 0, 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


INSERT INTO product_catalog (item_name, price, description, tag, std_id, active, `size`, created_by, last_modified_by, created_date, last_modified_date)
VALUES('School Diary', 200, '', '', null, 1, '', 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO product_catalog (item_name, price, description, tag, std_id, active, `size`, created_by, last_modified_by, created_date, last_modified_date)
VALUES('School Notebook', 60, '', '', null, 1, '', 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO product_catalog (item_name, price, description, tag, std_id, active, `size`, created_by, last_modified_by, created_date, last_modified_date)
VALUES('BookSet', 2200, '', '', (select id from class_details where std='UKG' and session_id=2), 1, '', 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO product_catalog (item_name, price, description, tag, std_id, active, `size`, created_by, last_modified_by, created_date, last_modified_date)
VALUES('FeeBook', 100, '', '', null, 1, '', 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO product_catalog (item_name, price, description, tag, std_id, active, `size`, created_by, last_modified_by, created_date, last_modified_date)
VALUES('English Language', 400, '', '',  (select id from class_details where std='UKG' and session_id=2), 1, '', 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO product_catalog (item_name, price, description, tag, std_id, active, `size`, created_by, last_modified_by, created_date, last_modified_date)
VALUES('Monthly Test Copy', 150, '', '', null, 1, '', 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO product_catalog (item_name, price, description, tag, std_id, active, `size`, created_by, last_modified_by, created_date, last_modified_date)
VALUES('School Pant Full', 350, '', 'Boy', null, 1, '24', 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO product_catalog (item_name, price, description, tag, std_id, active, `size`, created_by, last_modified_by, created_date, last_modified_date)
VALUES('School Midi ', 300, '', 'Girl', null, 1, '26', 'Sanket', 'Sanket', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
