CREATE TABLE topic_relation (
  id serial primary key,
  topic_id int DEFAULT '0',
  user_id int,
  item_id int DEFAULT '0',
  type varchar(16) NOT NULL,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

--;;
CREATE INDEX topic_relation_type ON topic_relation (type);
--;;
CREATE INDEX topic_relation_item_id ON topic_relation (item_id);
--;;
CREATE INDEX topic_relation_user_id ON topic_relation (user_id);
--;;
CREATE INDEX topic_relation_topic_id ON topic_relation (topic_id);
