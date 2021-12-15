CREATE TABLE feature (
  id serial primary key,
  title varchar(200),
  description text,
  article_count integer default 0,
  enabled smallint default 0,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

--;;
CREATE INDEX feature_title ON feature (title);
--;;
CREATE INDEX feature_enabled ON feature (enabled);
