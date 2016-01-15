create table stock_entry (
  id                        varchar(255) not null,
  account_id                    varchar(255) not null,
  application_id                varchar(255) not null,
  name                       varchar(255),
  sex                      varchar(255),
  age                       varchar(255),
  province                  varchar(255),
  city                    varchar(255),
  education                   varchar(255),
  phone                      varchar(255),
  mail                         varchar(255),
  marriage                   varchar(255),
  make_money                varchar(255),
  stock_year               varchar(255),
  self_introduce               varchar(255),
  views                     varchar(255),
  experience                  varchar(255),
  create_time            datetime not null,
  num                      integer,
  constraint pk_stock_entry primary key (id))
;

create table stock  (
  id                        varchar(255) not null,
  spell                      varchar(255),
  stock_name                     varchar(255),
  constraint pk_stock primary key(id))
;

create table stock_recommend (
  id                          varchar(255) not null,
  account_id                  varchar(255) not null,
  app_id                      varchar(255) not null,
  create_time                 datetime not null,
  stock                        varchar(255) not null,
  open                     decimal(65,2) ,
  close                 decimal(65,2),
  income                        decimal(65,2),
  current                      decimal(65,2),
  average_income                decimal(65,2),
  num                          int(10),
  total                       decimal(65,2),
  up                        int (10),
  down                        int(10),
  constraint pk_stock_recommend primary key(id))
;

create table stock_point (
  id                    varchar(255) not null,
  account_id                    varchar(255) not null,
  app_id                    varchar(255) not null,
  last_visit            datetime not null,
  point                       int(10) not null,
   constraint pk_stock_rank  primary key(id))
;

create table eat_entry (
  id                  varchar(255) not null,
  account_id           varchar(255) not null,
  app_id              varchar(255) not null,
  name                varchar(255) not null,
  sex                 int (1) ,
  age                 int(10),
  num                 integer,
  create_time          datetime not null,
  province            varchar(255),
  city                varchar(255),
  phone               varchar(255),
    constraint pk_eat_entry    primary key(id))
;

create table eat_question (
  id                  varchar(255) not null,
  question               varchar(255) not null,
  answer                varchar(255) not null,
  time                datetime not null,
  constraint pk_eat_question primary key(id))
  ;
  
  create table eat_record (
    id                varchar(255) not null,
    account_id           varchar(255) not null,
    app_id            varchar(255) not null,
    question_id       varchar(255) not null,
    answer            varchar(255) not null,
    is_shared         int(1) not null,
    result            int(1) not null,
    num               int(11) not null,
    answer_num        int(11) not null,
     constraint pk_eat_record primary key(id))
;

create table Announcement_feimei (
  id                        varchar(255) not null,
  account_id                    varchar(255) not null,
  app_id                varchar(255) not null,
  name                       varchar(255),
  sex                      varchar(255),
  phone                    varchar(255),
  address                    varchar(255),
  career                    varchar(255),
  head_image              varchar(255),
  mail                     varchar(255),
  words               varchar(255),
  create_time            datetime not null,
  isCancled                   int(1),
  is_bonded               int(1),
  born_date               datetime not null,
  constraint pk_stock_entry primary key (id))
;
  

alter table stock_recommend
add constraint FK_stock_recommend_account_id foreign key (account_id) references account(id)on delete restrict on update restrict;
alter table stock_recommend
add constraint FK_stock_recommend_app_id foreign key (app_id) references application(id)on delete restrict on update restrict;
alter table stock_recommend
add constraint FK_stock_recommend_stock foreign key (stock) references stock(id)on delete restrict on update restrict;
alter table stock_point add CONSTRAINT FK_stock_potin_app_id FOREIGN KEY (app_id) REFERENCES application(id) on delete RESTRICT on update RESTRICT
alter table eat_entry 
add constraint FK_eat_entry_account_id foreign key (account_id) references account(id) on delete restrict on update restrict;
alter table eat_entry 
add constraint FK_eat_entry_app_id foreign key (app_id) references application(id) on delete restrict on update restrict;
alter table eat_record 
add constraint FK_eat_record_question_id foreign key (question_id) references eat_question(id) on delete restrict on update restrict;
alter table eat_record
add constraint FK_eat_record_account_id foreign key (account_id) references account(id) on delete restrict on update restrict;
alter table eat_record
add constraint FK_eat_record_app_id foreign key (app_id) references application(id) on delete restrict on update restrict;