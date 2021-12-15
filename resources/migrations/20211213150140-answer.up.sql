CREATE TABLE answer (
  id serial primary key,
  question_id integer,
  content text ,
  against_count int NOT NULL DEFAULT '0' ,
  agree_count int NOT NULL DEFAULT '0' ,
  user_id int ,
  comment_count int DEFAULT '0' ,
  uninterested_count int DEFAULT '0' ,
  thanks_count int DEFAULT '0' ,
  category_id int DEFAULT '0' ,
  ip bigint DEFAULT NULL,
  force_fold smallint DEFAULT '0' ,
  anonymous smallint DEFAULT '0',
  publish_source varchar(16) DEFAULT NULL,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

--;;
CREATE INDEX answer_question_id ON answer (question_id);
--;;
CREATE INDEX answer_agree_count ON answer (agree_count);
--;;
CREATE INDEX answer_against_count ON answer (against_count);
--;;
CREATE INDEX answer_add_time ON answer (created_at);
--;;
CREATE INDEX answer_uid ON answer (user_id);
--;;
CREATE INDEX answer_uninterested_count ON answer (uninterested_count);
--;;
CREATE INDEX answer_force_fold ON answer (force_fold);
--;;
CREATE INDEX answer_anonymous ON answer (anonymous);
--;;
CREATE INDEX answer_publich_source ON answer (publish_source);
