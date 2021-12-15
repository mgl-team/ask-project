CREATE TABLE user_group (
  id serial primary key,
  type smallint DEFAULT '0' ,
  custom smallint DEFAULT '0' ,
  group_name varchar(50) NOT NULL,
  reputation_lower int DEFAULT '0',
  reputation_higer int DEFAULT '0',
  reputation_factor double precision DEFAULT '0'
)   ;

--;;
CREATE INDEX user_group_type ON user_group (type);
--;;
CREATE INDEX user_group_custom ON user_group (custom);
