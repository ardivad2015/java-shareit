CREATE TABLE IF NOT EXISTS users (
	user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	user_name VARCHAR(150) NOT NULL,
	email VARCHAR(50) NOT NULL,
	CONSTRAINT uq_user_email UNIQUE (email)
);

 CREATE TABLE  IF NOT EXISTS items (
            item_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            item_name VARCHAR(50) NOT NULL,
            description VARCHAR(255) NOT NULL,
            owner_id BIGINT NOT NULL,
            available BOOLEAN NOT NULL,
            CONSTRAINT owner_fk FOREIGN KEY (owner_id) REFERENCES users(user_id) ON DELETE RESTRICT
  );