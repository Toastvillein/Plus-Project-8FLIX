drop table reviews;

CREATE TABLE reviews (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         rate FLOAT(23) NOT NULL,
                         created_at DATETIME(6),
                         deleted_at DATETIME(6),
                         modified_at DATETIME(6),
                         movie_id BIGINT NOT NULL,
                         user_id BIGINT NOT NULL,
                         contents VARCHAR(300) NOT NULL,
                         PRIMARY KEY (id)
) ENGINE=InnoDB;
INSERT INTO reviews
(contents, created_at, deleted_at, modified_at, movie_id, rate, user_id)
VALUES
    ('재밌었어요', NOW(), NULL, NOW(), 1, 4.5, 1);

