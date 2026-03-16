-- ============================================================
--  Boss_FTC_RD - MySQL Setup
--  DATABASE NAME: boss_ftc_rd
--
--  Spring Boot auto-creates all tables when you run the app.
--  Just run this ONE line to create the database:
-- ============================================================

CREATE DATABASE IF NOT EXISTS boss_ftc_rd
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE boss_ftc_rd;

-- Tables are auto-created by Spring Boot JPA.
-- Run manually only if needed:

CREATE TABLE IF NOT EXISTS users (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    full_name           VARCHAR(200)  NOT NULL,
    email               VARCHAR(200)  NOT NULL UNIQUE,
    password_hash       VARCHAR(300)  NOT NULL,
    role                VARCHAR(50)   DEFAULT 'STUDENT',
    department          VARCHAR(100)  DEFAULT NULL,
    profile_photo       VARCHAR(300)  DEFAULT NULL,
    reset_token         VARCHAR(200)  DEFAULT NULL,
    reset_token_expiry  DATETIME      DEFAULT NULL,
    created_at          DATETIME      DEFAULT NULL,
    PRIMARY KEY (id),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS submissions (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    full_name        VARCHAR(200)  NOT NULL,
    email            VARCHAR(200)  NOT NULL,
    paper_id         VARCHAR(100)  NOT NULL,
    paper_title      VARCHAR(600)  NOT NULL,
    department       VARCHAR(100)  NOT NULL,
    author_type      VARCHAR(50)   DEFAULT NULL,
    category         VARCHAR(50)   NOT NULL,
    category_name    VARCHAR(200)  DEFAULT NULL,
    sub_type         VARCHAR(100)  DEFAULT NULL,
    proj_level       VARCHAR(100)  DEFAULT NULL,
    proj_rank        VARCHAR(50)   DEFAULT NULL,
    reward_amount    INT           DEFAULT 0,
    num_coauthors    INT           DEFAULT 0,
    uploaded_file    VARCHAR(500)  DEFAULT NULL,
    admin_reply      VARCHAR(2000) DEFAULT NULL,
    status           VARCHAR(50)   DEFAULT 'PENDING',
    submitted_at     DATETIME      NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_email    (email),
    INDEX idx_category (category),
    INDEX idx_status   (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SELECT 'boss_ftc_rd database ready!' AS Result;
SELECT 'Tables: users + submissions' AS Tables;
