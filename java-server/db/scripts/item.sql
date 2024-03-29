DROP TABLE IF EXISTS ITEM;
CREATE TABLE IF NOT EXISTS ITEM (
    object_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    item_id INT NOT NULL,
    count INT DEFAULT 1,
    enchant_level TINYINT DEFAULT 0,
    loc TINYINT NOT NULL,
    slot INT DEFAULT 0,
    price_sell INT DEFAULT 0,
    price_buy INT DEFAULT 0,
    CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES CHARACTER(id) ON DELETE CASCADE
);