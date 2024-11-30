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
            request_id BIGINT NOT NULL,
            available BOOLEAN NOT NULL,
            CONSTRAINT owner_fk FOREIGN KEY (owner_id) REFERENCES users(user_id) ON DELETE RESTRICT,
            CONSTRAINT  request_fk FOREIGN KEY (request_id) REFERENCES item_requests(request_id)
  );

  CREATE INDEX ON items (owner_id);

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
       CONSTRAINT date_fields CHECK (start_date < end_date AND start_date >= CURRENT_TIMESTAMP)
   );

 CREATE INDEX ON bookings (item_id);
 CREATE INDEX ON bookings (booker_id);

   CREATE TABLE IF NOT EXISTS comments
   (
      comment_id bigint  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
      item_id bigint NOT NULL,
      author_id bigint NOT NULL,
      created timestamp without time zone NOT NULL,
      text character varying NOT NULL,
      CONSTRAINT author_fk FOREIGN KEY (author_id) REFERENCES users (user_id),
      CONSTRAINT item_fk FOREIGN KEY (item_id) REFERENCES items (item_id)
   );

   CREATE INDEX ON comments (item_id);

   CREATE TABLE IF NOT EXISTS item_requests
      (
         request_id bigint  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
         author_id bigint NOT NULL,
         created timestamp without time zone NOT NULL,
         description VARCHAR(255) NOT NULL,
         CONSTRAINT author_fk FOREIGN KEY (author_id) REFERENCES users (user_id)
      );

   CREATE INDEX ON item_requests (author_id);