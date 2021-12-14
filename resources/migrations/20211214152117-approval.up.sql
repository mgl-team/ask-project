CREATE TABLE ask_approval (
  id serial primary key,
  item_id int DEFAULT '0',
  type varchar(16) NOT NULL,
  data text,
  user_id int DEFAULT '0',
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

--;;
CREATE INDEX approval_type ON ask_approval (type);
--;;
CREATE INDEX approval_user_id ON ask_approval (user_id);
--;;
CREATE INDEX approval_created_at ON ask_approval (created_at);
