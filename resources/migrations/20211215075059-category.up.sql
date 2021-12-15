CREATE TABLE category (
  id serial primary key,
  parent_id int default 0,
  title varchar(128) NOT NULL,
  type varchar(16) NOT NULL,
  sort smallint default 0,
  user_id int DEFAULT 0,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

--;;
CREATE INDEX category_parent_id ON category (parent_id);
--;;
CREATE INDEX category_title ON category (title);
