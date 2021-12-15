CREATE TABLE approval (
  id serial primary key,
  item_id int DEFAULT '0',
  type varchar(16) NOT NULL,
  data text,
  user_id int DEFAULT '0',
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

--;;
CREATE INDEX approval_type ON approval (type);
--;;
CREATE INDEX approval_user_id ON approval (user_id);
--;;
CREATE INDEX approval_created_at ON approval (created_at);
