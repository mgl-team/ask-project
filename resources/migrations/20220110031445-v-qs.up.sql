create or replace view v_question as
  select
    a.*,
    b.user_name,
    b.avatar_file
  from question a
  left join user_ex b
    on a.user_id = b.id;
