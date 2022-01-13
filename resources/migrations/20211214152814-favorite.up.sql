CREATE TABLE favorite (
  id serial primary key,
  item_id int DEFAULT '0',
  type varchar(16) NOT NULL,
  user_id int DEFAULT '0',
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

--;;
CREATE INDEX favorite_user_type ON favorite (user_id, type, item_id);
--;;
CREATE INDEX favorite_type ON favorite (type);
--;;
CREATE INDEX favorite_item_id ON favorite (item_id);
--;;
CREATE INDEX favorite_user_id ON favorite (user_id);
--;;
CREATE INDEX favorite_created_at ON favorite (created_at);
