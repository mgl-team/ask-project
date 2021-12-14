CREATE TABLE ask_report (
  id serial primary key,
  user_id int ,
  type varchar(16) NOT NULL,
  item_id int DEFAULT '0',
  reason text,
  url varchar(255) DEFAULT NULL,
  status smallint NOT NULL DEFAULT 0,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);
--;;
CREATE INDEX report_created_at ON ask_report (created_at);
--;;
CREATE INDEX status ON ask_report (status);
