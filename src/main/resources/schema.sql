DROP TABLE IF EXISTS config;
CREATE TABLE config
(
    id         varchar(36) PRIMARY KEY,
    created_at datetime,
    updated_at datetime,
    label      varchar(32),
    category   varchar(32),
    value      varchar(128)
);

DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id         varchar(36) PRIMARY KEY,
    created_at datetime,
    updated_at datetime,
    email      varchar(64) NOT NULL,
    password   varchar(64) NOT NULL,
    name       varchar(16) NOT NULL,
    role       varchar(16) NOT NULL,
    is_active  tinyint(1)  NOT NULL
);

DROP TABLE IF EXISTS sign_up_application;
CREATE TABLE sign_up_application
(
    id              varchar(36) PRIMARY KEY,
    created_at      datetime,
    updated_at      datetime,
    applicant_email varchar(64) NOT NULL,
    details         json        NOT NULL,
    status          varchar(16) NOT NULL,
    reject_reason   varchar(128)
);

DROP TABLE IF EXISTS activity_units;
CREATE TABLE activity_units
(
    id         varchar(36) PRIMARY KEY,
    created_at datetime,
    updated_at datetime,
    generation int         NOT NULL,
    position   varchar(16) NOT NULL,
    user_id    varchar(36) NOT NULL
);

DROP TABLE IF EXISTS user_alarm_settings;
CREATE TABLE user_alarm_settings
(
    id         varchar(36) PRIMARY KEY,
    created_at datetime,
    updated_at datetime,
    user_id    varchar(36) NOT NULL,
    device     tinyint(1)  NOT NULL,
    master     tinyint(1)  NOT NULL
);

DROP TABLE IF EXISTS user_devices;
CREATE TABLE user_devices
(
    id         varchar(36) PRIMARY KEY,
    created_at datetime,
    updated_at datetime,
    user_id    varchar(36)  NOT NULL,
    fcm_token  varchar(512) NOT NULL
);

DROP TABLE IF EXISTS schedules;
CREATE TABLE schedules
(
    id          binary(16) PRIMARY KEY,
    created_at  datetime,
    updated_at  datetime,
    is_deleted  tinyint(1)  NOT NULL,
    name        varchar(32) NOT NULL,
    description varchar(256),
    place       varchar(32) NOT NULL,
    date        date        NOT NULL,
    end_date    date,
    time        time,
    end_time    time,
    generation  int,
    type        varchar(32) NOT NULL
);

DROP TABLE IF EXISTS boards;
CREATE TABLE boards
(
    id             varchar(36) PRIMARY KEY,
    created_at     datetime,
    updated_at     datetime,
    board_type     varchar(255),
    notice_type    varchar(255),
    title          varchar(255),
    content        varchar(4000),
    display_target varchar(255),
    writer_id      varchar(36) NOT NULL,
    is_active      tinyint(1)  NOT NULL
);

DROP TABLE IF EXISTS generations;
CREATE TABLE generations
(
    value      int PRIMARY KEY,
    start_date date,
    end_date   date,
    is_active  tinyint(1)  NOT NULL
)
