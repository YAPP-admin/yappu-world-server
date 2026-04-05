DROP TABLE IF EXISTS config;
CREATE TABLE config
(
    name       varchar(64) PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    label      varchar(32),
    category   varchar(32),
    value      varchar(128)
);

DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id           binary(16) PRIMARY KEY,
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP,
    email        varchar(64) NOT NULL,
    password     varchar(64) NOT NULL,
    name         varchar(16) NOT NULL,
    role         varchar(16) NOT NULL,
    gender       varchar(8),
    phone_number varchar(16),
    is_active    NUMBER(1)  NOT NULL
);

DROP TABLE IF EXISTS sign_up_application;
CREATE TABLE sign_up_application
(
    id              binary(16) PRIMARY KEY,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    applicant_email varchar(64) NOT NULL,
    details         json        NOT NULL,
    applicant_name  varchar(64),
    status          varchar(16) NOT NULL,
    reject_reason   varchar(128)
);

DROP TABLE IF EXISTS sign_up_application_activity_unit;
CREATE TABLE sign_up_application_activity_unit
(
    id              binary(16)  PRIMARY KEY,
    created_at      datetime(6),
    updated_at      datetime(6),
    application_id  binary(16)  NOT NULL,
    generation      int         NOT NULL,
    position        varchar(16) NOT NULL
);

DROP TABLE IF EXISTS activity_units;
CREATE TABLE activity_units
(
    id         binary(16) PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    generation int         NOT NULL,
    position   varchar(16) NOT NULL,
    user_id    binary(16) NOT NULL
);

DROP TABLE IF EXISTS user_alarm_settings;
CREATE TABLE user_alarm_settings
(
    id         binary(16) PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    user_id    binary(16) NOT NULL,
    device     NUMBER(1)  NOT NULL,
    master     NUMBER(1)  NOT NULL
);

DROP TABLE IF EXISTS user_devices;
CREATE TABLE user_devices
(
    id         binary(16) PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    user_id    binary(16)  NOT NULL,
    fcm_token  varchar(512) NOT NULL
);

DROP TABLE IF EXISTS schedules;
CREATE TABLE schedules
(
    id           binary(16) PRIMARY KEY,
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP,
    is_deleted   NUMBER(1)  NOT NULL,
    name         varchar(32) NOT NULL,
    description  varchar(256),
    place        varchar(32),
    address      varchar(64),
    longitude    double,
    latitude     double,
    start_date   varchar(10) NOT NULL,
    end_date     varchar(10) NOT NULL,
    start_time   varchar(32),
    end_time     varchar(32),
    is_all_day   NUMBER(1)  NOT NULL,
    generation   int,
    type         varchar(32) NOT NULL,
    session_type varchar(32)
);

DROP TABLE IF EXISTS posts;
CREATE TABLE posts
(
    id              binary(16) PRIMARY KEY,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    type            varchar(255),
    notice_type     varchar(255),
    title           varchar(64),
    content         varchar(4000),
    content_summary varchar(255),
    display_target  varchar(255),
    writer_id       binary(16) NOT NULL,
    is_active       NUMBER(1) NOT NULL,
    session_id      binary(16)
);

DROP TABLE IF EXISTS generations;
CREATE TABLE generations
(
    value      int PRIMARY KEY,
    start_date varchar(10),
    end_date   varchar(10),
    is_active  NUMBER(1)  NOT NULL
);

drop table if exists attendances;
create table attendances
(
    id                 binary(16) PRIMARY KEY,
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP,
    user_id            binary(16) NOT NULL,
    schedule_id        binary(16) NOT NULL,
    status             varchar(32) NOT NULL,
    user_checked_in_at TIMESTAMP
);

drop table if exists late_passes;
create table late_passes
(
    id         binary(16) PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    user_id    binary(16) NOT NULL,
    generation int NOT NULL,
    count      int NOT NULL DEFAULT 0
);

DROP TABLE IF EXISTS teams;
CREATE TABLE teams
(
    id          binary(16) PRIMARY KEY,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    generation  int NOT NULL,
    name        varchar(255) NOT NULL,
    has_app     NUMBER(1) NOT NULL DEFAULT 0,
    has_web     NUMBER(1) NOT NULL DEFAULT 0
);

DROP TABLE IF EXISTS team_services;
CREATE TABLE team_services
(
    id              binary(16) PRIMARY KEY,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    has_app         NUMBER(1) NOT NULL DEFAULT 0,
    has_web         NUMBER(1) NOT NULL DEFAULT 0,
    name            varchar(255) DEFAULT NULL,
    service_links   json DEFAULT NULL,
    team_id         binary(16) NOT NULL
);

DROP TABLE IF EXISTS team_members;
CREATE TABLE team_members
(
    id               binary(16) PRIMARY KEY,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP,
    activity_unit_id binary(16) NOT NULL,
    team_id          binary(16) NOT NULL
);
