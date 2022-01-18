create or replace view v_report as
  select
    a.id,
    a.reason,
    a.created_at as reported_at,
    a.type,
    a.item_id,
    a.user_id as reported_user_id,
    case
      when a.type = 'answer'
      then b.user_id

      when a.type = 'comment'
      then c.user_id
      end
      as user_id,
    case
      when a.type = 'answer'
      then b.content

      when a.type = 'comment'
      then c.message
      end
      as message
  from report a
  left join answer b on a.item_id = b.id and a.type = 'answer'
  left join comments c on a.item_id = c.id and a.type = 'comment'
  where a.status = 0;
--;;
