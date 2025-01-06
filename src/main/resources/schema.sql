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

DROP TABLE IF EXISTS user_sign_up_applications;
CREATE TABLE user_sign_up_applications
(
    id              varchar(36) PRIMARY KEY,
    created_at      datetime,
    updated_at      datetime,
    applicant_email varchar(64) NOT NULL,
    application     json        NOT NULL,
    status          varchar(16) NOT NULL,
    reject_reason   varchar(128)
)
