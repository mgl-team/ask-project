CREATE TABLE ask_vote (
  id serial primary key,
  item_id int DEFAULT '0',
  type varchar(16) NOT NULL,
  user_id int DEFAULT 0,
  vote_value smallint NOT NULL,
  reputation_factor int default 0,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
) ;

--;;
CREATE INDEX vote_type_item_id ON ask_vote (type, item_id);
--;;
CREATE INDEX vote_value ON ask_vote (vote_value);
