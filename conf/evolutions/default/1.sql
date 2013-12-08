# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table app_user (
  app_user_id               bigint not null,
  create_date               timestamp,
  update_date               timestamp,
  create_ip                 varchar(255),
  update_ip                 varchar(255),
  name                      varchar(255),
  last_login                timestamp,
  email                     varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  language                  varchar(7),
  type                      varchar(8),
  email_validated           boolean,
  active                    boolean,
  activation_token          text,
  constraint ck_app_user_language check (language in ('Spanish','Russian','French','Italian','German','English')),
  constraint ck_app_user_type check (type in ('admin','none','customer')),
  constraint pk_app_user primary key (app_user_id))
;

create table file_tag (
  file_tag_id               bigint not null,
  create_date               timestamp,
  update_date               timestamp,
  create_ip                 varchar(255),
  update_ip                 varchar(255),
  tag_name                  varchar(255),
  tag_rank                  integer,
  constraint pk_file_tag primary key (file_tag_id))
;

create table linked_account (
  linked_account_id         bigint not null,
  app_user_id               bigint,
  provider_user_id          varchar(255),
  provider_key              varchar(255),
  constraint pk_linked_account primary key (linked_account_id))
;

create table security_permission (
  security_permission_id    bigint not null,
  value                     varchar(255),
  constraint pk_security_permission primary key (security_permission_id))
;

create table security_role (
  security_role_id          bigint not null,
  role_name                 varchar(255),
  constraint pk_security_role primary key (security_role_id))
;

create table token_action (
  id                        bigint not null,
  token                     varchar(255),
  target_user_app_user_id   bigint,
  type                      varchar(2),
  created                   timestamp,
  expires                   timestamp,
  constraint ck_token_action_type check (type in ('EV','PR')),
  constraint uq_token_action_token unique (token),
  constraint pk_token_action primary key (id))
;

create table user_file (
  user_file_id              bigint not null,
  create_date               timestamp,
  update_date               timestamp,
  create_ip                 varchar(255),
  update_ip                 varchar(255),
  file_name                 varchar(255),
  file_content              text,
  app_user_id               bigint,
  constraint pk_user_file primary key (user_file_id))
;


create table app_user_file_tag (
  app_user_id                    bigint not null,
  file_tag_id                    bigint not null,
  constraint pk_app_user_file_tag primary key (app_user_id, file_tag_id))
;

create table app_user_security_role (
  app_user_id                    bigint not null,
  security_role_id               bigint not null,
  constraint pk_app_user_security_role primary key (app_user_id, security_role_id))
;

create table app_user_security_permission (
  app_user_id                    bigint not null,
  security_permission_id         bigint not null,
  constraint pk_app_user_security_permission primary key (app_user_id, security_permission_id))
;

create table user_file_file_tag (
  file_tag_id                    bigint not null,
  user_file_id                   bigint not null,
  constraint pk_user_file_file_tag primary key (file_tag_id, user_file_id))
;
create sequence app_user_seq;

create sequence file_tag_seq;

create sequence linked_account_seq;

create sequence security_permission_seq;

create sequence security_role_seq;

create sequence token_action_seq;

create sequence user_file_seq;

alter table linked_account add constraint fk_linked_account_appUser_1 foreign key (app_user_id) references app_user (app_user_id);
create index ix_linked_account_appUser_1 on linked_account (app_user_id);
alter table token_action add constraint fk_token_action_targetUser_2 foreign key (target_user_app_user_id) references app_user (app_user_id);
create index ix_token_action_targetUser_2 on token_action (target_user_app_user_id);
alter table user_file add constraint fk_user_file_appUser_3 foreign key (app_user_id) references app_user (app_user_id);
create index ix_user_file_appUser_3 on user_file (app_user_id);



alter table app_user_file_tag add constraint fk_app_user_file_tag_app_user_01 foreign key (app_user_id) references app_user (app_user_id);

alter table app_user_file_tag add constraint fk_app_user_file_tag_file_tag_02 foreign key (file_tag_id) references file_tag (file_tag_id);

alter table app_user_security_role add constraint fk_app_user_security_role_app_01 foreign key (app_user_id) references app_user (app_user_id);

alter table app_user_security_role add constraint fk_app_user_security_role_sec_02 foreign key (security_role_id) references security_role (security_role_id);

alter table app_user_security_permission add constraint fk_app_user_security_permissi_01 foreign key (app_user_id) references app_user (app_user_id);

alter table app_user_security_permission add constraint fk_app_user_security_permissi_02 foreign key (security_permission_id) references security_permission (security_permission_id);

alter table user_file_file_tag add constraint fk_user_file_file_tag_file_ta_01 foreign key (file_tag_id) references file_tag (file_tag_id);

alter table user_file_file_tag add constraint fk_user_file_file_tag_user_fi_02 foreign key (user_file_id) references user_file (user_file_id);

# --- !Downs

drop table if exists app_user cascade;

drop table if exists app_user_file_tag cascade;

drop table if exists app_user_security_role cascade;

drop table if exists app_user_security_permission cascade;

drop table if exists file_tag cascade;

drop table if exists user_file_file_tag cascade;

drop table if exists linked_account cascade;

drop table if exists security_permission cascade;

drop table if exists security_role cascade;

drop table if exists token_action cascade;

drop table if exists user_file cascade;

drop sequence if exists app_user_seq;

drop sequence if exists file_tag_seq;

drop sequence if exists linked_account_seq;

drop sequence if exists security_permission_seq;

drop sequence if exists security_role_seq;

drop sequence if exists token_action_seq;

drop sequence if exists user_file_seq;

