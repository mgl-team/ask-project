create or replace view v_add_approve as
  select *
  from approval
  where status = 0;
