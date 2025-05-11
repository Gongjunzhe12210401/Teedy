CREATE TABLE user_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    reason TEXT,
    request_status VARCHAR(10) NOT NULL,
    create_date TIMESTAMP NOT NULL
);
