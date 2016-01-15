# --- !Ups

create table article (
  id                        varchar(255) not null,
  article_id                   varchar(255),
  article_name             varchar(255),
  author_id                 varchar(255),
  user_id                       varchar(255),
  app_id                    varchar(255),
  author_name               varchar(255),
  is_verified                   tinyint(1) default 0,
  is_special                 tinyint(1) default 0,
  author_image              varchar(255),
  article_content           varchar(512),
  image_number              integer,
  image_url1                varchar(255),
  image_url2                varchar(255),
  image_url3                varchar(255),
  image_url4                varchar(255),
  image_url5                varchar(255),
  image_url6                varchar(255),
  image_url7                varchar(255),
  image_url8                varchar(255),
  image_url9                varchar(255),
  create_time               datetime not null,
  update_time               datetime not null,
  last_reply                datetime,
  theme_id                            varchar(255),
  theme_name                     varchar(255),
  theme_image                    varchar(255),
  theme_class             integer,
  is_elite                  tinyint(1) default 0,
  is_top                    tinyint(1) default 0,
  is_public               tinyint(1) default 0,
  is_new_comments    tinyint(1) default 0,
  comments_number    integer,
  thumbs_number        integer,
  thumbs_number2      integer,
  constraint pk_article primary key (id))
;

create table comments (
  id                        varchar(255) not null,
  article_id                   varchar(255),
  comments                   varchar(255),
  author_id                 varchar(255),
  user_id                   varchar(255),
  app_id                    varchar(255),
  author_name               varchar(255),
  author_image              varchar(255),
  topic_id                  varchar(255),
  comments_content           varchar(2000),
  create_time               datetime not null,
  update_time               datetime not null,
  last_reply                datetime,
  is_hidden                 tinyint(1) default 0,
  is_top                    tinyint(1) default 0,
  constraint pk_comments primary key (id))
;

create table comments_level2 (
  id                        varchar(255) not null,
  article_id                   varchar(255),
  comments                   varchar(255),
  author_id                 varchar(255),
  user_id                   varchar(255),
  app_id                    varchar(255),
  author_name               varchar(255),
  author_image              varchar(255),
  comments_content           varchar(2000),
  create_time               datetime not null,
  update_time               datetime not null,
  last_reply                datetime,
  is_hidden                 tinyint(1) default 0,
  is_top                    tinyint(1) default 0,
  constraint pk_comments_level2 primary key (id))
;

create table theme (
  id                        bigint auto_increment not null,
  app_id                    varchar(255),
  theme_name                     varchar(255),
  hot_people                   varchar(255),
  theme_image               varchar(255),
  theme_class              integer,
  last_update               datetime,
  constraint pk_theme primary key (id))
;

create table my_theme (
  id                        varchar(255) not null,
  account_id                varchar(255),
  app_id                    varchar(255),
  theme_id                            varchar(255),
  theme_name                     varchar(255),
  hot_people                        varchar(255),
  theme_image                    varchar(255),
   theme_class              integer,
  is_new                     integer,
  latest_visit             datetime,
  create_time          datetime,
  constraint pk_my_theme primary key (id))
;

create table thumbs (
  id                        varchar(255) not null,
  article_id                   varchar(255),
  comment_id           varchar(255),
  user_id                   varchar(255),
  thumbs                    varchar(255),
  constraint pk_thumbs primary key (id))
;

create table sign (
  id                        varchar(255) not null,
  account_id                varchar(255),
  app_id                    varchar(255),
  last_visit              datetime,
  sign_charm        integer,
  constraint pk_sign primary key (id))
;

create table collections (
  id                        varchar(255) not null,
  article_id                   varchar(255),
  user_id                   varchar(255),
  article_name          varchar(255),
  constraint pk_collections primary key (id))
;

alter table article add constraint fk_article_author_52 foreign key (author_id) references account (id) on delete restrict on update restrict;
create index ix_article_author_52 on article (author_id);
alter table article add constraint fk_article_application_53 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_article_application_53 on article (app_id);
create index ix_article_create_time_54 on article (create_time);

alter table comments add constraint fk_comments_author_55 foreign key (author_id) references account (id) on delete restrict on update restrict;
create index ix_comments_author_55 on comments (author_id);
alter table comments add constraint fk_comments_application_56 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_comments_application_56 on comments (app_id);
create index ix_comments_create_time_57 on comments (create_time);

alter table theme add constraint fk_theme_application_58 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_theme_application_58 on theme (app_id);

alter table my_theme add constraint fk_my_theme_account_59 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_my_theme_account_59 on my_theme (account_id);
alter table my_theme add constraint fk_my_theme_application_60 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_my_theme_application_60 on my_theme (app_id);
create index ix_my_theme_create_time_61 on my_theme (create_time);

alter table thumbs add constraint fk_thumbs_user_62 foreign key (user_id) references account (id) on delete restrict on update restrict;
create index ix_thumbs_user_62 on thumbs (user_id);

alter table sign add constraint fk_sign_user_63 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_sign_user_63 on sign (account_id);
alter table sign add constraint fk_sign_application_64 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_sign_application_64 on sign (app_id);

alter table collections add constraint fk_collections_user_65 foreign key (user_id) references account (id) on delete restrict on update restrict;
create index ix_collections_user_65 on collections (user_id);

alter table comments_level2 add constraint fk_comments_level2_author_55 foreign key (author_id) references account (id) on delete restrict on update restrict;
create index ix_comments_level2_author_55 on comments_level2 (author_id);
alter table comments_level2 add constraint fk_comments_level2_application_56 foreign key (app_id) references application (id) on delete restrict on update restrict;
create index ix_comments_level2_application_56 on comments_level2 (app_id);
create index ix_comments_level2_create_time_57 on comments_level2 (create_time);
# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table article;

drop table comments;

drop table theme;

drop table my_theme;

drop table thumbs;

drop table sign;

drop table collections;

drop table comments_level2;

SET FOREIGN_KEY_CHECKS=1;
