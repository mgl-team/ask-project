CREATE TABLE topic (
  id serial primary key,
  title varchar(64) DEFAULT NULL ,
  discuss_count int DEFAULT '0' ,
  description text ,
  pic varchar(255) DEFAULT NULL ,
  lock smallint NOT NULL DEFAULT '0' ,
  focus_count int DEFAULT '0' ,
  user_related smallint DEFAULT '0' ,
  parent_id int DEFAULT '0',
  is_parent smallint DEFAULT '0',
  discuss_count_last_week int DEFAULT '0',
  discuss_count_last_month int DEFAULT '0',
  discuss_count_update int DEFAULT '0',
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
) ;
--;;
CREATE UNIQUE INDEX topic_title ON topic (title);
--;;
CREATE INDEX topic_discuss_count ON topic (discuss_count);
--;;
CREATE INDEX topic_created_at ON topic (created_at);
--;;
CREATE INDEX topic_user_related ON topic (user_related);
--;;
CREATE INDEX topic_focus_count ON topic (focus_count);
--;;
CREATE INDEX topic_lock ON topic (lock);
--;;
CREATE INDEX topic_parent_id ON topic (parent_id);
--;;
CREATE INDEX topic_is_parent ON topic (is_parent);
--;;
CREATE INDEX topic_discuss_count_last_week ON topic (discuss_count_last_week);
--;;
CREATE INDEX topic_discuss_count_last_month ON topic (discuss_count_last_month);
--;;
CREATE INDEX topic_discuss_count_update ON topic (discuss_count_update);
