CREATE TABLE comments (
  id serial primary key,
  item_id int DEFAULT '0',
  user_id int DEFAULT '0',
  type varchar(16) NOT NULL,
  message text,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
) ;

--;;

CREATE INDEX comments_type_item_id ON comments (type, item_id);
--;;
CREATE INDEX comments_created_at ON comments (created_at);
--;;
