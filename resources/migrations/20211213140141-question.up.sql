CREATE TABLE question (
  id serial primary key,
  question_content varchar(255) NOT NULL DEFAULT '' ,
  question_detail text ,
  user_id int DEFAULT NULL ,
  answer_count int NOT NULL DEFAULT '0' ,
  answer_users int NOT NULL DEFAULT '0' ,
  view_count int NOT NULL DEFAULT '0' ,
  focus_count int NOT NULL DEFAULT '0' ,
  comment_count int NOT NULL DEFAULT '0' ,
  action_history_id int NOT NULL DEFAULT '0' ,
  category_id int NOT NULL DEFAULT '0' ,
  agree_count int NOT NULL DEFAULT '0' ,
  against_count int NOT NULL DEFAULT '0' ,
  best_answer int NOT NULL DEFAULT '0' ,
  unverified_modify text,
  unverified_modify_count int NOT NULL DEFAULT '0',
  ip bigint DEFAULT NULL,
  last_answer int NOT NULL DEFAULT '0' ,
  lock smallint NOT NULL DEFAULT '0' ,
  anonymous smallint NOT NULL DEFAULT '0',
  thanks_count int NOT NULL DEFAULT '0',
  is_recommend smallint NOT NULL DEFAULT '0',
  sort smallint check (sort > 0) NOT NULL DEFAULT '0',
  accept_answer_id int DEFAULT NULL ,
  pay_money decimal(10,0) DEFAULT NULL ,
  pay_type smallint DEFAULT NULL,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);
--;;
CREATE INDEX question_category_id ON question (category_id);
--;;
CREATE INDEX question_published_uid ON question (user_id);
--;;
CREATE INDEX question_answer_count ON question (answer_count);
--;;
CREATE INDEX question_agree_count ON question (agree_count);
--;;
CREATE INDEX question_question_content ON question (question_content);
--;;
CREATE INDEX question_lock ON question (lock);
--;;
CREATE INDEX question_thanks_count ON question (thanks_count);
--;;
CREATE INDEX question_anonymous ON question (anonymous);
--;;
CREATE INDEX question_best_answer ON question (best_answer);
--;;
CREATE INDEX question_against_count ON question (against_count);
--;;
CREATE INDEX question_is_recommend ON question (is_recommend);
--;;
CREATE INDEX question_sort ON question (sort);
