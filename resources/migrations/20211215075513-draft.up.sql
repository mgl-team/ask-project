CREATE TABLE draft (
  id serial primary key,
  item_id int DEFAULT '0',
  type varchar(16),
  user_id int DEFAULT '0',
  data text,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

--;;
CREATE INDEX draft_item_id ON draft (item_id);
--;;
CREATE INDEX draft_user_id ON draft (user_id);
--;;
CREATE INDEX draft_created_at ON draft (created_at);
