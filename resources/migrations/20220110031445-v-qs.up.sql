create or replace view v_question as
  select
    a.id,
    a.question_content,
    a.question_detail,
    a.user_id,
    a.answer_count,
    a.answer_users,
    a.view_count,
    a.focus_count,
    a.comment_count,
    a.agree_count,
    a.against_count,
    a.best_answer,
    b.user_name,
    b.avatar_file
  from question a
  left join user_ex b
    on a.user_id = b.id
  where a.question_content != ''; 
