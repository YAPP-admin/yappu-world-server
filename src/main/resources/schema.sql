DROP TABLE IF EXISTS config;
CREATE TABLE config
(
    id         varchar(36) PRIMARY KEY,
    created_at datetime,
    updated_at datetime,
    value      varchar(64)
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
    is_active  tinyint(1) NOT NULL
);

DROP TABLE IF EXISTS sign_up_application;
CREATE TABLE sign_up_application
(
    id                varchar(36) PRIMARY KEY,
    created_at        datetime,
    updated_at        datetime,
    applicant_email   varchar(64) NOT NULL,
    applicant_details json        NOT NULL,
    status            varchar(16) NOT NULL,
    reject_reason     varchar(128)
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
