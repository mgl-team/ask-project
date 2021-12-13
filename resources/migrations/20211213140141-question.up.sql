CREATE TABLE ask_question (
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

CREATE INDEX category_id ON ask_question (category_id);
CREATE INDEX update_time ON ask_question (update_time);
CREATE INDEX add_time ON ask_question (add_time);
CREATE INDEX published_uid ON ask_question (uid);
CREATE INDEX answer_count ON ask_question (answer_count);
CREATE INDEX agree_count ON ask_question (agree_count);
CREATE INDEX question_content ON ask_question (question_content);
CREATE INDEX lock ON ask_question (lock);
CREATE INDEX thanks_count ON ask_question (thanks_count);
CREATE INDEX anonymous ON ask_question (anonymous);
CREATE INDEX popular_value ON ask_question (popular_value);
CREATE INDEX best_answer ON ask_question (best_answer);
CREATE INDEX against_count ON ask_question (against_count);
CREATE INDEX is_recommend ON ask_question (is_recommend);
CREATE INDEX received_email_id ON ask_question (received_email_id);
CREATE INDEX sort ON ask_question (sort);
