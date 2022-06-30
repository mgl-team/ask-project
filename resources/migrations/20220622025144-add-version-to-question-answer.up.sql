alter table question add column version int DEFAULT 0 NOT NULL;
--;;
alter table answer add column version int DEFAULT 0 NOT NULL;
