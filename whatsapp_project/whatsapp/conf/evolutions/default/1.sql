# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table groups (
  id                            bigint auto_increment not null,
  group_name_username           varchar(255),
  username_username             varchar(255),
  constraint pk_groups primary key (id)
);

create table messages (
  id                            bigint auto_increment not null,
  sender_username               varchar(255),
  reciever_username             varchar(255),
  message                       TEXT,
  timestamp                     timestamp DEFAULT CURRENT_TIMESTAMP,
  requested                     integer default 0,
  is_read                       integer,
  constraint pk_messages primary key (id)
);

create table users (
  username                      varchar(255) not null,
  password                      varchar(255),
  token                         varchar(255) not null,
  name                          varchar(255),
  is_group                      integer,
  constraint uq_users_token unique (token),
  constraint pk_users primary key (username)
);

alter table groups add constraint fk_groups_group_name_username foreign key (group_name_username) references users (username) on delete restrict on update restrict;
create index ix_groups_group_name_username on groups (group_name_username);

alter table groups add constraint fk_groups_username_username foreign key (username_username) references users (username) on delete restrict on update restrict;
create index ix_groups_username_username on groups (username_username);

alter table messages add constraint fk_messages_sender_username foreign key (sender_username) references users (username) on delete restrict on update restrict;
create index ix_messages_sender_username on messages (sender_username);

alter table messages add constraint fk_messages_reciever_username foreign key (reciever_username) references users (username) on delete restrict on update restrict;
create index ix_messages_reciever_username on messages (reciever_username);


# --- !Downs

alter table groups drop foreign key fk_groups_group_name_username;
drop index ix_groups_group_name_username on groups;

alter table groups drop foreign key fk_groups_username_username;
drop index ix_groups_username_username on groups;

alter table messages drop foreign key fk_messages_sender_username;
drop index ix_messages_sender_username on messages;

alter table messages drop foreign key fk_messages_reciever_username;
drop index ix_messages_reciever_username on messages;

drop table if exists groups;

drop table if exists messages;

drop table if exists users;

