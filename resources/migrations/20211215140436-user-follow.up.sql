CREATE TABLE user_follow (
  id serial primary key,
  user_id int,
  followd_user_id int,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
)   ;
--;;
CREATE INDEX user_follow_user_id ON user_follow (user_id);
--;;
CREATE INDEX user_follow_followd_user_id ON user_follow (followd_user_id);
--;;
CREATE INDEX user_follow_user_followd_id ON user_follow (user_id,followd_user_id);
