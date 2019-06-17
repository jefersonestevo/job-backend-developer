-- CREATE DATABASE DEMO;

CREATE TABLE JBD_USER (
    ID SERIAL CONSTRAINT USER_KEY PRIMARY KEY,
    LOGIN VARCHAR(256) NOT NULL,
    PASSWORD VARCHAR(512) NOT NULL
);

CREATE TABLE JBD_USER_INFO (
    ID SERIAL CONSTRAINT USER_INFO_KEY PRIMARY KEY,
    USER_ID INT NOT NULL CONSTRAINT USER_INFO_USER REFERENCES JBD_USER(ID),
    NAME VARCHAR(256),
    LAST_NAME VARCHAR(256),
    EMAIL VARCHAR(512),
    PHONE_NUMBER VARCHAR(512)
);

CREATE TABLE JBD_USER_ADDRESS (
    ID SERIAL CONSTRAINT USER_ADDRESS_KEY PRIMARY KEY,
    USER_ID INT NOT NULL CONSTRAINT USER_ADDRESS_USER REFERENCES JBD_USER(ID),
    STREET VARCHAR(256),
    NUMBER VARCHAR(64),
    CITY VARCHAR(128),
    STATE VARCHAR(64),
    COUNTRY VARCHAR(64)
);

CREATE TABLE JBD_USER_PICTURE (
    ID SERIAL CONSTRAINT USER_PICTURE_KEY PRIMARY KEY,
    USER_ID INT NOT NULL CONSTRAINT USER_PICTURE_USER REFERENCES JBD_USER(ID),
    PICTURE BYTEA NOT NULL
);

CREATE INDEX IDX_USER_LOGIN ON JBD_USER(LOGIN);
CREATE INDEX IDX_USER_INFO_USER ON JBD_USER_INFO(USER_ID);
CREATE INDEX IDX_USER_ADDRESS_USER ON JBD_USER_ADDRESS(USER_ID);
CREATE INDEX IDX_USER_PICTURE_USER ON JBD_USER_PICTURE(USER_ID);

