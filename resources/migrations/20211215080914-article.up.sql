CREATE TABLE article (
  id serial primary key,
  title varchar(200),
  content text,
  user_id integer not null,
  comments integer default 0,
  views integer default 0,
  lock smallint default 0,
  votes integer default 0,
  category_id integer,
  is_recommend smallint default 0,
  sort smallint default 0,
  source varchar(100),
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
) ;

--;;
CREATE INDEX article_title ON article (title);
--;;
CREATE INDEX article_user_id ON article (user_id);
