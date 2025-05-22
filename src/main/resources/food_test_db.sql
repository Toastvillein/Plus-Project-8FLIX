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
VALUES (1,NOW(),NOW(),NULL);

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
       (2,10,1,2,NOW(),NOW(),NULL),
       (3,10,1,1,NOW(),NOW(),NULL),
       (4,10,1,5,NOW(),NOW(),NULL);