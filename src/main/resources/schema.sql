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

  CREATE TABLE IF NOT EXISTS bookings
   (
       booking_id bigint  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
       item_id bigint NOT NULL,
       booker_id bigint NOT NULL,
       start_date timestamp without time zone NOT NULL,
       end_date timestamp without time zone NOT NULL,
       status character varying(15) NOT NULL,
       CONSTRAINT booker_fk FOREIGN KEY (booker_id) REFERENCES users (user_id) ON DELETE RESTRICT,
       CONSTRAINT item_fk FOREIGN KEY (item_id) REFERENCES items (item_id) ON DELETE RESTRICT,
       CONSTRAINT data_fields CHECK (start_date < end_date AND start_date > CURRENT_DATE AND end_date > CURRENT_DATE)
   )