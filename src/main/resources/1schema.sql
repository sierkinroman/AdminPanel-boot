--drop database if exists admin_panel_boot;
--create database admin_panel_boot;
--use admin_panel_boot;

create table if not exists role (
	id int primary key auto_increment not null,
	name varchar(30) not null unique
 );

create table if not exists user (
	id int primary key auto_increment not null,
	username varchar(100) not null unique,
	password varchar(100) not null,
	email varchar(100) not null unique,
	first_name varchar(100) not null,
	last_name varchar(100) not null,
	role_id int,
	foreign key (role_id) references role(id)
);

delete from user;
delete from role;

insert into role(name) values ("ROLE_ADMIN");
insert into role(name) values ("ROLE_USER");
