CREATE TABLE ask_focus (
  id serial primary key,
  item_id int DEFAULT '0',
  type varchar(16) NOT NULL,
  user_id int DEFAULT '0',
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
) ;


CREATE INDEX focus_type_item_id ON ask_focus (type, item_id);
CREATE INDEX focus_user_id ON ask_focus (type,item_id, user_id);
