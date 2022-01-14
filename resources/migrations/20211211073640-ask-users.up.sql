CREATE TABLE user_ex (
  id serial primary key,
  user_name varchar(255) DEFAULT NULL ,
  salt varchar(16) DEFAULT NULL ,
  avatar_file varchar(128) DEFAULT NULL ,
  sex smallint DEFAULT NULL ,
  birthday int DEFAULT NULL ,
  country varchar(64) DEFAULT NULL ,
  province varchar(64) DEFAULT NULL ,
  city varchar(64) DEFAULT NULL ,
  job_id int DEFAULT '0' ,
  notification_unread int NOT NULL DEFAULT '0' ,
  inbox_unread int NOT NULL DEFAULT '0' ,
  inbox_recv int NOT NULL DEFAULT '0' ,
  focus_count int NOT NULL DEFAULT '0' ,
  friend_count int NOT NULL DEFAULT '0' ,
  invite_count int NOT NULL DEFAULT '0' ,
  article_count int NOT NULL DEFAULT '0' ,
  question_count int NOT NULL DEFAULT '0' ,
  answer_count int NOT NULL DEFAULT '0' ,
  topic_focus_count int NOT NULL DEFAULT '0' ,
  invitation_available int NOT NULL DEFAULT '0' ,
  group_id int DEFAULT '0' ,
  reputation_group int DEFAULT '0' ,
  forbidden smallint DEFAULT '0' ,
  agree_count int DEFAULT '0' ,
  thanks_count int DEFAULT '0' ,
  views_count int DEFAULT '0' ,
  reputation int DEFAULT '0' ,
  reputation_update_time int DEFAULT '0' ,
  integral int DEFAULT '0',
  draft_count int DEFAULT NULL,
  default_timezone varchar(32) DEFAULT NULL,
  recent_topics text
);

--;;
CREATE INDEX user_user_user_name ON user_ex (user_name);
--;;
CREATE INDEX user_user_reputation ON user_ex (reputation);
--;;
CREATE INDEX user_user_reputation_update_time ON user_ex (reputation_update_time);
--;;
CREATE INDEX user_user_group_id ON user_ex (group_id);
--;;
CREATE INDEX user_user_agree_count ON user_ex (agree_count);
--;;
CREATE INDEX user_user_thanks_count ON user_ex (thanks_count);
--;;
CREATE INDEX user_user_forbidden ON user_ex (forbidden);
--;;
CREATE INDEX user_user_integral ON user_ex (integral);
--;;
CREATE INDEX user_user_answer_count ON user_ex (answer_count);
