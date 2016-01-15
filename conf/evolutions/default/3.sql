# --- !Ups

create table shop (
  id                        varchar(255) not null,
  app_id                    varchar(255) not null,
  name                      varchar(255) not null,
  address                   varchar(255),
  ibeacon_sn                varchar(255),
constraint pk_shop primary key (id))
;

create table shake_record (
  id                        varchar(255) not null,
  shop_id                   varchar(255) not null,
  user_id                   varchar(255) not null,
  create_time               datetime not null,
constraint pk_shake_record primary key (id))
;

create table coupon (
  id                        varchar(255) not null,
  app_id                    varchar(255) not null,
  name                      varchar(255) not null,
  type                      integer not null,
  create_time               datetime not null,
  date_type                 integer not null,
  available_time            datetime,
  expired_in_days           integer not null,
  code                      varchar(255) not null,
  picture                   varchar(255),
  batch                     integer not null,
constraint pk_coupon primary key (id))
;

create table coupon_record (
  id                        varchar(255) not null,
  user_id                   varchar(255) not null,
  coupon_id                 varchar(255) not null,
  is_drawed                 tinyint(1) default 0,
  is_used                   tinyint(1) default 0,
  create_time               datetime not null,
constraint pk_coupon_record primary key (id))
;

create table share_record (
  id                        varchar(255) not null,
  app_id                    varchar(255) not null,
  user_id                   varchar(255) not null,
  share_url                 varchar(255) not null,
  create_time               datetime not null,
constraint pk_share_record primary key (id))
;

create table user_address (
  account_id                varchar(50) not null,
  app_id                    varchar(50) not null,
  name                      varchar(100) not null,
  phone_number              varchar(20)  not null,
  code                      varchar(10)  not null,
  address                   varchar(255) not null,
constraint pk_user_address primary key (account_id, app_id))
;

create table share_log (
  id                        integer auto_increment not null,
  domain                    varchar(255),
  path                      varchar(255),
  user_id                   varchar(255),
  share_by                  varchar(255),  
constraint pk_share_log primary key (id))
;

create table activity_entry (
  id                        char(255) not null,
  name                      char(255),
  application               char(255),
  age                       char(255),
  phone                     char(255),
  mail                      char(255),
  selfintroduce             char(255),
  career                    char(255),
  talent                    char(255),
  habit                     char(255),
  advantage                 char(255),
  impress                   char(255),
  body                      char(255),
  education                 char(255),
  type                      char(255),
  jiabin                    char(255),
  words                     char(255),
  largepic                  char(255),
  smallpic                  char(255),
  video                     char(255),
  create_time               datetime not null,
  num                       int(3),
  video_url                 char(255),
constraint pk_activity_entry primary key (id))
;

create table announcement(
  id                        char(255) not null,
  name                      char(255),
  data                      char(255),
  time                      char(255),
constraint pk_announcement primary key(id))
;

create table beautiful(
  id                        int(4) not null,
  name                      char(200) not null,
constraint pk_beautiful primary key(id))
;

create table education(
  id                        int(4) not null,
  edu                      char(255) not null,
constraint pk_education primary key(id))
;

create table type(
  id                        int(4) not null,
  type                      char(255) not null,
constraint pk_type primary key(id))
;

create table chat_room_announcement(
  announcement                        char(255),
)
;

create table hot_people_pic(
  id                         varchar(255) not null,
  name                       varchar(255),
  num                        varchar(255),
  pic_url                    varchar(255),
  create_time                datetime,
constraint pk_hot_people_pic primary key(id))
;

create table hot_people_mp3(
  id                         varchar(255) not null,
  name                       varchar(255),
  num                        varchar(255),
  music_url                    varchar(255),
  create_time                datetime,
  main_url                   varchar(255),
constraint pk_hot_people_mp3 primary key(id))
;

create table hot_people_rank(
  num                        varchar(255) not null,
  hot_url                 varchar(255),
  pic_url                    varchar(255),
  name                       varchar(255),
  words                      varchar(255),
constraint pk_hot_people_rank primary key(num))
;

alter table user_address add constraint fk_user_address_account_49 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_user_address_account_49 on user_address (account_id);
alter table user_address add constraint fk_user_address_app_50 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_user_address_app_50 on user_address (app_id);

alter table shop add constraint fk_shop_application_38 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_shop_application_38 on shop (app_id);

alter table shake_record add constraint fk_shake_record_shop_39 foreign key (shop_id) references shop (id) on delete restrict on update restrict;
create index ix_shake_record_shop_39 on shake_record (shop_id);

alter table shake_record add constraint fk_shake_record_user_id_40 foreign key (user_id) references account (id) on delete restrict on update restrict;
create index ix_shake_record_user_id_40 on shake_record (user_id);
create index ix_shake_record_create_time_42 on shake_record (create_time);

alter table coupon add constraint fk_coupon_app_id_43 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_coupon_app_id_43 on coupon (app_id);
create index ix_coupon_create_time_44 on coupon (create_time);

alter table coupon_record add constraint fk_coupon_record_user_id_46 foreign key (user_id) references account (id) on delete restrict on update restrict;
create index ix_coupon_record_user_id_46 on coupon_record (user_id);
alter table coupon_record add constraint fk_coupon_record_coupon_id_47 foreign key (coupon_id) references coupon (id) on delete restrict on update restrict;
create index ix_coupon_record_coupon_id_47 on coupon_record (coupon_id);
create index ix_coupon_record_create_time_48 on coupon_record (create_time);

alter table share_record add constraint fk_share_record_app_id_49 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_share_record_app_id_49 on share_record (app_id);
alter table share_record add constraint fk_share_record_user_id_50 foreign key (user_id) references account (id) on delete restrict on update restrict;
create index ix_share_record_user_id_50 on share_record (user_id);
create index ix_share_record_create_time_51 on share_record (create_time);

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table shop;

drop table shake_record;

drop table coupon;

drop table coupon_record;

drop table share_record;

SET FOREIGN_KEY_CHECKS=1;
