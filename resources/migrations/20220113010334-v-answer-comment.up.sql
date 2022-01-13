create or replace view v_answer_comment as
  select
    a.id,
    a.user_id,
    a.message,
    a.created_at,
    b.user_name,
    b.avatar_file
  from comments a
  left join user_ex b
    on a.user_id = b.id
  where a.type = 'answer';
