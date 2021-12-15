CREATE TABLE user_invitation (
  id serial primary key,
  user_id int,
  invitation_code varchar(32) DEFAULT NULL ,
  invitation_email varchar(255) DEFAULT NULL ,
  add_ip varchar(20) DEFAULT NULL ,
  active_expire smallint DEFAULT '0' ,
  active_time int DEFAULT NULL ,
  active_ip bigint DEFAULT NULL ,
  active_status smallint DEFAULT '0' ,
  active_user_id int DEFAULT NULL,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
)  ;

--;;
CREATE INDEX user_invitation_user_id ON user_invitation (user_id);
--;;
CREATE INDEX user_invitation_invitation_code ON user_invitation (invitation_code);
--;;
CREATE INDEX user_invitation_invitation_email ON user_invitation (invitation_email);
--;;
CREATE INDEX user_invitation_active_time ON user_invitation (active_time);
--;;
CREATE INDEX user_invitation_active_ip ON user_invitation (active_ip);
--;;
CREATE INDEX user_invitation_active_status ON user_invitation (active_status);
