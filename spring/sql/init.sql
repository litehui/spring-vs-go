CREATE DATABASE IF NOT EXISTS user_db;
CREATE DATABASE IF NOT EXISTS shop_db;
CREATE DATABASE IF NOT EXISTS product_db;
CREATE DATABASE IF NOT EXISTS favorite_db;

\c user_db;

CREATE TABLE IF NOT EXISTS buyer (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    avatar VARCHAR(500),
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS seller (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    company_name VARCHAR(200),
    contact_name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    business_license VARCHAR(100),
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS seller_sub_account (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL REFERENCES seller(id),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    role VARCHAR(50) DEFAULT 'operator',
    permissions TEXT[],
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

\c shop_db;

CREATE TABLE IF NOT EXISTS shop (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    logo VARCHAR(500),
    category VARCHAR(100),
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

\c product_db;

CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    category VARCHAR(100),
    images TEXT[],
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

\c favorite_db;

CREATE TABLE IF NOT EXISTS favorite_product (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(buyer_id, product_id)
);

CREATE TABLE IF NOT EXISTS favorite_shop (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    shop_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(buyer_id, shop_id)
);
