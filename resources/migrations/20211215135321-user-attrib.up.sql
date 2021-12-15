CREATE TABLE user_attrib (
  id serial primary key,
  user_id integer,
  introduction varchar(255) DEFAULT NULL ,
  signature varchar(255) DEFAULT NULL
)   ;

--;;
CREATE INDEX user_attrib_user_id ON user_attrib (user_id);
