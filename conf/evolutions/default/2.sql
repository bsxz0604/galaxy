# --- !Ups

create table app_profile_gift (
  id                        varchar(255) not null,
  account_id                varchar(255) not null,
  app_id                    varchar(255) not null,
  create_time               datetime not null,
  gift_id                   bigint not null,
  gift_number               integer,
constraint pk_app_profile_gift primary key (id))
;

create table gift (
  id                        bigint auto_increment not null,
  app_id                    varchar(255) not null,
  gift_image                 varchar(255),
  gift_name                 varchar(255),
  gift_free_time            integer,
  price                     integer,
  effect                    integer,
  constraint pk_gift primary key (id))
;

create table badge (
  id                        bigint auto_increment not null,
  app_id                    varchar(255) not null,
  badge_image               varchar(255),
  badge_grey_image          varchar(255),
  badge_class               varchar(255),
  badge_name                varchar(255),
  value                     integer,
  constraint pk_gift primary key (id))
;

create table money (
  account_id                varchar(255) not null,
  app_id                    varchar(255) not null,
  operation_time            datetime not null,
  money                     integer,
  constraint pk_money primary key (account_id, app_id))
;

create table money_record (
  id                        bigint auto_increment not null,
  create_time               datetime not null,
  account_id                varchar(255) not null,
  app_id                    varchar(255) not null,
  action                    varchar(255),
  money                     integer,
constraint pk_money_record primary key (id))
;

create table charm_value (
  id                       varchar(255) not null,
  create_time              datetime not null,
  sender_id                varchar(255) not null,
  receiver_id              varchar(255) not null,
  app_id                   varchar(255) not null,
  charm_value              integer,
  total_charm_value        integer,
constraint pk_charm_value primary key(id));

create table chat_room_time (
  id                       varchar(255) not null,
  room_id                  varchar(255) not null,
  account_id               varchar(255) not null,
  action_time              datetime not null,
  action                   varchar(255),
  send_gift_number         integer,
constraint pk_chat_room_time primary key(id));

alter table app_profile_gift add constraint fk_app_profile_gift_account_24 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_app_profile_gift_account_24 on app_profile_gift (account_id);
alter table app_profile_gift add constraint fk_app_profile_gift_application_25 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_app_profile_gift_application_25 on app_profile_gift (app_id);
alter table app_profile_gift add constraint fk_app_profile_gift_gift_26 foreign key (gift_id) references gift (id) on delete restrict on update restrict;
create index ix_app_profile_gift_gift_26 on app_profile_gift (gift_id);
create index ix_app_profile_gift_create_time_27 on app_profile_gift (create_time);

alter table gift add constraint fk_gift_application_28 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_gift_application_28 on gift (app_id);

alter table badge add constraint fk_badge_application_28 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_badge_application_28 on badge (app_id);

alter table money add constraint fk_money_account_29 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_money_account_29 on money (account_id);
alter table money add constraint fk_money_application_30 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_money_application_30 on money (app_id);
create index ix_money_operation_time_31 on money (operation_time);

alter table money_record add constraint fk_money_record_account_32 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_money_record_account_32 on money_record (account_id);
alter table money_record add constraint fk_money_record_application_33 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_money_record_application_33 on money_record (app_id);
create index ix_money_record_create_time_34 on money_record (create_time);

alter table charm_value add constraint fk_charm_value_account_35 foreign key (sender_id) references account (id) on delete restrict on update restrict;
create index ix_charm_value_account_35 on charm_value (sender_id);
alter table charm_value add constraint fk_charm_value_account_36 foreign key (receiver_id) references account (id) on delete restrict on update restrict;
create index ix_charm_value_account_36 on charm_value (receiver_id);
alter table charm_value add constraint fk_charm_value_application_37 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_charm_value_application_37 on charm_value (app_id);
create index ix_charm_value_create_time_38 on charm_value (create_time);

alter table chat_room_time add constraint fk_chat_room_time_account_39 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_chat_room_time_account_39 on chat_room_time (account_id);
create index ix_chat_room_time_action_time_40 on chat_room_time (action_time);

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table app_profile_gift;

drop table gift;

drop table money;

drop table money_record;

drop table charm_value;

drop table chat_room_time;

SET FOREIGN_KEY_CHECKS=1;
