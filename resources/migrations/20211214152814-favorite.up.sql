CREATE TABLE ask_favorite (
  id serial primary key,
  item_id int DEFAULT '0',
  type varchar(16) NOT NULL,
  user_id int DEFAULT '0',
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

--;;
CREATE INDEX favorite_type ON ask_favorite (type);
--;;
CREATE INDEX favorite_item_id ON ask_favorite (item_id);
--;;
CREATE INDEX favorite_user_id ON ask_favorite (user_id);
--;;
CREATE INDEX favorite_created_at ON ask_favorite (created_at);
