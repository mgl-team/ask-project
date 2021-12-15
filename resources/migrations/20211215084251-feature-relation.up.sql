CREATE TABLE feature_relation (
  id serial primary key,
  feature_id int DEFAULT '0',
  article_id int DEFAULT '0',
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

--;;
CREATE INDEX feature_relation_article_id ON feature_relation (article_id);
--;;
CREATE INDEX feature_relation_feature_id ON feature_relation (feature_id);
