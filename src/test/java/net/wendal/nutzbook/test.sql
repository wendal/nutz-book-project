/* create.sql */		 
 DROP TABLE IF EXISTS t_accounts$year;
 CREATE TABLE t_accounts$year (
 accounts_ID1 varchar(18) default NULL ,
 accounts_ID2 varchar(18) default NULL ,
 accounts_name varchar(200) default NULL ,
 accounts_level char(2) default NULL ,
 parent_code varchar(16) default NULL,
 create_time datetime default NULL,
 upd_date datetime default NULL,
 valid_flag char(1) default NULL ,
 extend_flag char(2) default NULL ,
 accounts_year char(4) default NULL,
 subject_month char(2) default NULL ,
 hl_flag char(1) default NULL ,
 inc_exp_flag char(1) default NULL ,
 id int(11) NOT NULL auto_increment ,
 upd_user_id varchar(50) default NULL ,
 index(accounts_ID2),
 PRIMARY KEY  (id)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
 /* create.sql2*/
 select 1;