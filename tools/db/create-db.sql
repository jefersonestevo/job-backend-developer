-- CREATE DATABASE user_info;

CREATE TABLE JBD_USER (
    ID SERIAL CONSTRAINT JBD_USER_KEY PRIMARY KEY,
    LOGIN VARCHAR(256) NOT NULL,
    PASSWORD VARCHAR(512) NOT NULL,
    ENABLED BOOLEAN NOT NULL
);

CREATE INDEX IDX_JBD_USER_LOGIN ON JBD_USER(LOGIN);

CREATE TABLE JBD_USER_ROLE (
    ID SERIAL CONSTRAINT JBD_USER_ROLE_KEY PRIMARY KEY,
    LOGIN VARCHAR(256) NOT NULL,
    ROLE VARCHAR(64) NOT NULL
);

CREATE INDEX IDX_JBD_USER_ROLE_LOGIN ON JBD_USER_ROLE(LOGIN);

CREATE TABLE JBD_USER_INFO (
    ID SERIAL CONSTRAINT JBD_USER_INFO_KEY PRIMARY KEY,
    USER_ID INT NOT NULL CONSTRAINT USER_INFO_USER REFERENCES JBD_USER(ID),
    NAME VARCHAR(256),
    LAST_NAME VARCHAR(256),
    EMAIL VARCHAR(512),
    PHONE_NUMBER VARCHAR(512)
);

CREATE INDEX IDX_JBD_USER_INFO_USER ON JBD_USER_INFO(USER_ID);

CREATE TABLE JBD_USER_ADDRESS (
    ID SERIAL CONSTRAINT JBD_USER_ADDRESS_KEY PRIMARY KEY,
    USER_ID INT NOT NULL CONSTRAINT USER_ADDRESS_USER REFERENCES JBD_USER(ID),
    STREET VARCHAR(256),
    NUMBER VARCHAR(64),
    CITY VARCHAR(128),
    STATE VARCHAR(64),
    COUNTRY VARCHAR(64)
);

CREATE INDEX IDX_JBD_USER_ADDRESS_USER ON JBD_USER_ADDRESS(USER_ID);

CREATE TABLE JBD_USER_IMPORT (
    ID INT
);

CREATE UNIQUE INDEX IDX_JBD_USER_IMPORT_KEY ON JBD_USER_IMPORT(ID);

