--drop table specimen_file;
--;;
create table attach_file (
	id serial primary key,
  attach_id int,
  attach_type_id int,
  attach_type text,
  filename text,
	url text,
	created_at timestamp default now()
);
--;;
create index attach_file_index_type1 on attach_file(attach_id, attach_type);
--;;
create index attach_file_index_type2 on attach_file(attach_id, attach_type_id);
