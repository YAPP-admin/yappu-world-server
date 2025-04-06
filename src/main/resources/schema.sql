DROP TABLE IF EXISTS config;
CREATE TABLE config
(
    name       varchar(64) PRIMARY KEY,
    created_at datetime(6),
    updated_at datetime(6),
    label      varchar(32),
    category   varchar(32),
    value      varchar(128)
);

DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id         binary(16) PRIMARY KEY,
    created_at datetime(6),
    updated_at datetime(6),
    email      varchar(64) NOT NULL,
    password   varchar(64) NOT NULL,
    name       varchar(16) NOT NULL,
    role       varchar(16) NOT NULL,
    is_active  tinyint(1)  NOT NULL
);

DROP TABLE IF EXISTS sign_up_application;
CREATE TABLE sign_up_application
(
    id              binary(16) PRIMARY KEY,
    created_at      datetime(6),
    updated_at      datetime(6),
    applicant_email varchar(64) NOT NULL,
    details         json        NOT NULL,
    status          varchar(16) NOT NULL,
    reject_reason   varchar(128)
);

DROP TABLE IF EXISTS activity_units;
CREATE TABLE activity_units
(
    id         binary(16) PRIMARY KEY,
    created_at datetime(6),
    updated_at datetime(6),
    generation int         NOT NULL,
    position   varchar(16) NOT NULL,
    user_id    binary(16) NOT NULL
);

DROP TABLE IF EXISTS user_alarm_settings;
CREATE TABLE user_alarm_settings
(
    id         binary(16) PRIMARY KEY,
    created_at datetime(6),
    updated_at datetime(6),
    user_id    binary(16) NOT NULL,
    device     tinyint(1)  NOT NULL,
    master     tinyint(1)  NOT NULL
);

DROP TABLE IF EXISTS user_devices;
CREATE TABLE user_devices
(
    id         binary(16) PRIMARY KEY,
    created_at datetime(6),
    updated_at datetime(6),
    user_id    binary(16)  NOT NULL,
    fcm_token  varchar(512) NOT NULL
);

DROP TABLE IF EXISTS schedules;
CREATE TABLE schedules
(
    id           binary(16) PRIMARY KEY,
    created_at   datetime(6),
    updated_at   datetime(6),
    is_deleted   tinyint(1)  NOT NULL,
    name         varchar(32) NOT NULL,
    description  varchar(256),
    place        varchar(32),
    date         date        NOT NULL,
    end_date     date        NOT NULL,
    time         time(6),
    end_time     time(6),
    is_all_day   tinyint(1)  NOT NULL,
    generation   int,
    type         varchar(32) NOT NULL,
    session_type varchar(32)
);

DROP TABLE IF EXISTS boards;
CREATE TABLE boards
(
    id              binary(16) PRIMARY KEY,
    created_at      datetime(6),
    updated_at      datetime(6),
    board_type      varchar(255),
    notice_type     varchar(255),
    title           varchar(64),
    content         varchar(4000),
    content_summary varchar(255),
    display_target  varchar(255),
    writer_id       binary(16) NOT NULL,
    is_active       tinyint(1)  NOT NULL
);

DROP TABLE IF EXISTS posts;
CREATE TABLE posts
(
    id              binary(16) PRIMARY KEY,
    created_at      datetime(6),
    updated_at      datetime(6),
    type            varchar(255),
    notice_type     varchar(255),
    title           varchar(64),
    content         varchar(4000),
    content_summary varchar(255),
    display_target  varchar(255),
    writer_id       binary(16) NOT NULL,
    is_active       tinyint(1)  NOT NULL
);

DROP TABLE IF EXISTS generations;
CREATE TABLE generations
(
    value      int PRIMARY KEY,
    start_date date,
    end_date   date,
    is_active  tinyint(1)  NOT NULL
)
