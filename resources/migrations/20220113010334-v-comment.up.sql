create or replace view v_comment as
  select
    a.id,
    a.item_id,
    a.type,
    a.user_id,
    a.message,
    a.created_at,
    b.user_name,
    b.avatar_file
  from comments a
  left join user_ex b
    on a.user_id = b.id;
