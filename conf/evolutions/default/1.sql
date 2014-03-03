# --- First database schema

# --- !Ups

set ignorecase true;

create table number (
  id                        bigint not null,
  name                      varchar(255) not null unique,
  phoneNumber               varchar(255) not null unique,
  constraint pk_number primary key (id))
;

create sequence number_seq start with 1000;

create index ix_num_name on number (name);


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists number;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists number_seq;
