CREATE TABLE uninterested (
  id serial primary key,
  item_id int DEFAULT '0',
  type varchar(16) NOT NULL,
  user_id int DEFAULT '0',
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
) ;

--;;
CREATE INDEX uninterested_type_item_id ON uninterested (type, item_id);
