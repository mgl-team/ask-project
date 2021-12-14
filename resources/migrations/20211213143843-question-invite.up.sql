CREATE TABLE ask_question_invite (
  id serial primary key,
  question_id int,
  sender_id int,
  cecipients_id int,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
) ;


--;;
CREATE INDEX question_invite_question_id ON ask_question_invite (question_id);
--;;
CREATE INDEX question_invite_sender_id ON ask_question_invite (sender_id);
--;;
CREATE INDEX question_invite_cecipients_id ON ask_question_invite (cecipients_id);
