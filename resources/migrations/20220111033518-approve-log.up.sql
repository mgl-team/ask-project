alter table approval add column status smallint DEFAULT 0 NOT NULL;
--;;
CREATE TABLE approval_log (
  approve_id int DEFAULT '0',
  status int DEFAULT '0',
  data text,
  reason text,
  approve_user_id int DEFAULT '0',
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

--;;
CREATE INDEX approval_log_approve_id ON approval_log (approve_id);
--;;
CREATE INDEX approval_log_status ON approval_log (status);
--;;
CREATE INDEX approval_log_approve_user_id ON approval_log (approve_user_id);
--;;
CREATE INDEX approval_log_created_at ON approval_log (created_at);
