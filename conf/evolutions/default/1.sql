# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account (
  id                        varchar(255) not null,
  username                  varchar(255) not null,
  password                  varchar(255),
  type                      integer not null,
  create_time               datetime not null,
  role                      integer not null,
  constraint uq_account_username unique (username),
  constraint pk_account primary key (id))
;

create table activity (
  id                        varchar(255) not null,
  app_id                    varchar(255) not null,
  start_time                datetime not null,
  end_time                  datetime not null,
  img_url                   varchar(255),
  link                      varchar(255),
  repeatable                tinyint(1) default 0 not null,
  type                      integer not null,
  constraint pk_activity primary key (id))
;

create table activity_choice (
  id                        varchar(255) not null,
  activity_content_id       varchar(255) not null,
  type                      integer not null,
  choice                    varchar(255) not null,
  description               varchar(255),
  constraint pk_activity_choice primary key (id))
;

create table activity_content (
  id                        varchar(255) not null,
  activity_id               varchar(255) not null,
  img_url                   varchar(255),
  introduction              varchar(255),
  max_selection             integer,
  constraint pk_activity_content primary key (id))
;

create table activity_result (
  id                        varchar(255) not null,
  activity_content_id       varchar(255) not null,
  account_id                varchar(255) not null,
  choice_id                 varchar(255) not null,
  create_time               datetime not null,
  constraint pk_activity_result primary key (id))
;

create table album (
  id                        varchar(255) not null,
  app_id                    varchar(255) not null,
  user_id                   varchar(255) not null,
  img_url                   varchar(255) not null,
  description               varchar(255),
  constraint pk_album primary key (id))
;

create table app_profile (
  search_id                 bigint not null AUTO_INCREMENT,
  account_id                varchar(255) not null,
  app_id                    varchar(255) not null,
  name                      varchar(255) not null,
  head_image                varchar(255),
  sex                       integer,
  birthday                  datetime,
  constellation_id          integer,
  career                    varchar(255),
  address                   varchar(255),
  idiograph                 varchar(255),
  is_verified               tinyint(1) default 0 not null,
  constraint uq_app_profile_1 unique (search_id, app_id),
  constraint pk_app_profile primary key (search_id, account_id, app_id))
;

create table application (
  id                        varchar(255) not null,
  app_name                  varchar(255) not null,
  constraint uq_application_app_name unique (app_name),
  constraint pk_application primary key (id))
;

create table banner (
  id                        varchar(255) not null,
  app_id                    varchar(255) not null,
  url                       varchar(255) not null,
  type                      integer not null,
  link                      varchar(255),
  constraint pk_banner primary key (id))
;

create table constellation (
  id                        integer auto_increment not null,
  name                      varchar(255) not null,
  img_url                   varchar(255),
  constraint uq_constellation_name unique (name),
  constraint pk_constellation primary key (id))
;

create table interest_fan (
  id                        varchar(255) not null,
  interester_id             varchar(255) not null,
  app_id                    varchar(255) not null,
  interestee_id             varchar(255) not null,
  constraint uq_interest_fan_1 unique (interester_id,app_id,interestee_id),
  constraint pk_interest_fan primary key (id))
;

create table logs_bean (
  id                        bigint auto_increment not null,
  user_id                   varchar(255),
  operation_time            varchar(255),
  request                   varchar(2000),
  response                  varchar(255),
  method                    varchar(255),
  url                       varchar(255),
  ip                        varchar(255),
  constraint pk_logs_bean primary key (id))
;

create table nationality (
  id                        integer auto_increment not null,
  nation                    varchar(255) not null,
  short_name                varchar(255),
  constraint uq_nationality_nation unique (nation),
  constraint pk_nationality primary key (id))
;

create table program (
  id                        varchar(255) not null,
  app_id                    varchar(255) not null,
  title                     varchar(255) not null,
  show_time                 datetime not null,
  constraint pk_program primary key (id))
;

create table program_content (
  id                        varchar(255) not null,
  program_id                varchar(255) not null,
  type                      integer not null,
  content                   varchar(255) not null,
  constraint pk_program_content primary key (id))
;

alter table activity add constraint fk_activity_app_1 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_activity_app_1 on activity (app_id);
alter table activity_choice add constraint fk_activity_choice_content_2 foreign key (activity_content_id) references activity_content (id) on delete restrict on update restrict;
create index ix_activity_choice_content_2 on activity_choice (activity_content_id);
alter table activity_content add constraint fk_activity_content_activity_3 foreign key (activity_id) references activity (id) on delete restrict on update restrict;
create index ix_activity_content_activity_3 on activity_content (activity_id);
alter table activity_result add constraint fk_activity_result_content_4 foreign key (activity_content_id) references activity_content (id) on delete restrict on update restrict;
create index ix_activity_result_content_4 on activity_result (activity_content_id);
alter table activity_result add constraint fk_activity_result_account_5 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_activity_result_account_5 on activity_result (account_id);
alter table activity_result add constraint fk_activity_result_choice_6 foreign key (choice_id) references activity_choice (id) on delete restrict on update restrict;
create index ix_activity_result_choice_6 on activity_result (choice_id);
alter table album add constraint fk_album_app_7 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_album_app_7 on album (app_id);
alter table album add constraint fk_album_account_8 foreign key (user_id) references account (id) on delete restrict on update restrict;
create index ix_album_account_8 on album (user_id);
alter table app_profile add constraint fk_app_profile_account_9 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_app_profile_account_9 on app_profile (account_id);
alter table app_profile add constraint fk_app_profile_application_10 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_app_profile_application_10 on app_profile (app_id);
alter table app_profile add constraint fk_app_profile_constellation_11 foreign key (constellation_id) references constellation (id) on delete restrict on update restrict;
create index ix_app_profile_constellation_11 on app_profile (constellation_id);
alter table banner add constraint fk_banner_app_15 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_banner_app_15 on banner (app_id);
alter table program_content add constraint fk_program_content_program_17 foreign key (program_id) references program (id) on delete restrict on update restrict;
create index ix_program_content_program_17 on program_content (program_id);
alter table interest_fan add constraint fk_interest_fan_account_18 foreign key (interester_id) references account (id) on delete restrict on update restrict;
create index ix_interest_fan_account_18 on interest_fan (interester_id);
alter table interest_fan add constraint fk_interest_fan_account_19 foreign key (interestee_id) references account (id) on delete restrict on update restrict;
create index ix_interest_fan_account_19 on interest_fan (interestee_id);
alter table interest_fan add constraint fk_interest_fan_application_20 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_interest_fan_application_20 on interest_fan (app_id);
create index ix_create_time_account_21 on account (create_time);
create index ix_show_time_program_22 on program (show_time);
create index ix_create_time_activity_result_23 on activity_result (create_time);


# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table account;

drop table activity;

drop table activity_choice;

drop table activity_content;

drop table activity_result;

drop table album;

drop table app_profile;

drop table application;

drop table banner;

drop table constellation;

drop table interest_fan;

drop table logs_bean;

drop table nationality;

drop table program;

drop table program_content;

drop table result_group;

SET FOREIGN_KEY_CHECKS=1;

