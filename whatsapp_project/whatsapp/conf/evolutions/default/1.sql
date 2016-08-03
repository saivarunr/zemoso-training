# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table messages (
  id                            bigint auto_increment not null,
  sender_username               varchar(255),
  reciever_username             varchar(255),
  message                       TEXT,
  timestamp                     timestamp DEFAULT CURRENT_TIMESTAMP,
  constraint pk_messages primary key (id)
);

create table users (
  username                      varchar(255) not null,
  password                      varchar(255),
  token                         varchar(255) not null,
  constraint uq_users_token unique (token),
  constraint pk_users primary key (username)
);

alter table messages add constraint fk_messages_sender_username foreign key (sender_username) references users (username) on delete restrict on update restrict;
create index ix_messages_sender_username on messages (sender_username);

alter table messages add constraint fk_messages_reciever_username foreign key (reciever_username) references users (username) on delete restrict on update restrict;
create index ix_messages_reciever_username on messages (reciever_username);


# --- !Downs

alter table messages drop foreign key fk_messages_sender_username;
drop index ix_messages_sender_username on messages;

alter table messages drop foreign key fk_messages_reciever_username;
drop index ix_messages_reciever_username on messages;

drop table if exists messages;

drop table if exists users;

