SET FOREIGN_KEY_CHECKS = 0;

drop table IF EXISTS cart_item;
drop table IF EXISTS cart;
drop table IF EXISTS food;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE food (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                   name VARCHAR(50) NOT NULL,
                   quantity int NOT NULL ,
                   food_status VARCHAR(20) NOT NULL,
                   created_at DATETIME(6),
                   modified_at DATETIME(6),
                   deleted_at DATETIME(6)
) ENGINE=InnoDB;

INSERT INTO food (id, name, quantity, food_status, created_at, modified_at, deleted_at)
VALUES (1,"팝콘",1000,"FOR_SALE",NOW(),NOW(),NULL),
       (2,"나쵸",1000,"FOR_SALE",NOW(),NOW(),NULL),
       (3,"콜라",1000,"FOR_SALE",NOW(),NOW(),NULL),
       (4,"사이다",1000,"FOR_SALE",NOW(),NOW(),NULL),
       (5,"환타",0,"SOLD_OUT",NOW(),NOW(),NULL);

CREATE TABLE cart (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                   created_at DATETIME(6),
                   modified_at DATETIME(6),
                   deleted_at DATETIME(6)
) ENGINE=InnoDB;

INSERT INTO cart(id, created_at, modified_at, deleted_at)
VALUES (1,NOW(),NOW(),NULL),
       (2,NOW(),NOW(),NULL),
       (3,NOW(),NOW(),NULL),
       (4,NOW(),NOW(),NULL),
       (5,NOW(),NOW(),NULL),
       (6,NOW(),NOW(),NULL),
       (7,NOW(),NOW(),NULL),
       (8,NOW(),NOW(),NULL),
       (9,NOW(),NOW(),NULL),
       (10,NOW(),NOW(),NULL),
       (11,NOW(),NOW(),NULL);

CREATE TABLE cart_item (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        quantity int NOT NULL ,
                        cart_id BIGINT NOT NULL,
                        food_id BIGINT NOT NULL ,
                        CONSTRAINT fk_item_cart FOREIGN KEY (cart_id) REFERENCES cart(id),
                        CONSTRAINT fk_item_food FOREIGN KEY (food_id) REFERENCES food(id),
                        created_at DATETIME(6),
                        modified_at DATETIME(6),
                        deleted_at DATETIME(6),
                        UNIQUE (cart_id,food_id)
) ENGINE=InnoDB;

INSERT INTO cart_item(id, quantity, cart_id, food_id, created_at, modified_at, deleted_at)
VALUES (1,10,1,3,NOW(),NOW(),NULL),
       (2,1000,1,2,NOW(),NOW(),NULL),
       (3,10,1,1,NOW(),NOW(),NULL),
       (4,1,2,1,NOW(),NOW(),NULL),
       (5,1,3,1,NOW(),NOW(),NULL),
       (6,1,4,1,NOW(),NOW(),NULL),
       (7,1,5,1,NOW(),NOW(),NULL),
       (8,1,6,1,NOW(),NOW(),NULL),
       (9,1,7,1,NOW(),NOW(),NULL),
       (10,1,8,1,NOW(),NOW(),NULL),
       (11,1,9,1,NOW(),NOW(),NULL),
       (12,1,10,1,NOW(),NOW(),NULL),
       (13,1,11,1,NOW(),NOW(),NULL);
