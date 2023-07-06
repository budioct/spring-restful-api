show databases;

CREATE DATABASE belajar_spring_restful_api;

show databases;

use belajar_spring_restful_api;

# membuat table users dengan username sebagai primary key dan token unique.. jadi supaya kita ingin user bisa login dimana saja kita
# taruh session di table users, pada column token_expired_at
CREATE TABLE users
(
    username         VARCHAR(100) NOT NULL,
    password         VARCHAR(100) NOT NULL,
    name             VARCHAR(100) NOT NULL,
    token            VARCHAR(100),
    token_expired_at BIGINT,
    PRIMARY KEY (username),
    UNIQUE (token)
) ENGINE InnoDB;

describe users;

select * from users;


# table contacts berlasi dengan users dengan kardinalitas 1 users banyak contants. reference username
CREATE TABLE contacts
(
    id         VARCHAR(100) NOT NULL,
    username   VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100),
    phone      VARCHAR(100),
    email      VARCHAR(100),
    PRIMARY KEY (id),
    FOREIGN KEY fk_contacts_users (username) REFERENCES users (username)
) Engine InnoDB;

describe contacts;

select * from contacts;


CREATE TABLE addresses
(
    id          VARCHAR(100) NOT NULL,
    contact_id  VARCHAR(100) NOT NULL,
    street      VARCHAR(200),
    city        VARCHAR(100),
    province    VARCHAR(100),
    country     VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10),
    PRIMARY KEY (id),
    FOREIGN KEY fk_addresses_contacts (contact_id) REFERENCES contacts (id)
) ENGINE InnoDB;

describe addresses;


select * from users;
select * from contacts;
select * from addresses;
