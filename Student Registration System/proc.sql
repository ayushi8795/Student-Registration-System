delete from logs;
drop sequence s_log;
create sequence s_log
start with 100
increment by 1
nocache;

drop package dbproject;
create or replace package dbproject as
  procedure sh_ta(ta_cursor OUT sys_refcursor);
  procedure sh_student(student_cursor OUT sys_refcursor);
  procedure sh_class(class_cursor OUT sys_refcursor);
  procedure sh_course(course_cursor OUT sys_refcursor);
  procedure sh_enrollment(enrollment_cursor OUT sys_refcursor);
  procedure sh_prerequisite(prerequisite_cursor OUT sys_refcursor);
  procedure sh_log(log_cursor OUT sys_refcursor);
  procedure ta_classes(class_id IN classes.classid%type, ta_class_cursor OUT sys_refcursor, ret_message OUT varchar2);
  procedure pre_course(dcode in prerequisites.dept_code%type, cno in prerequisites.course# %type, rc OUT sys_refcursor);
  procedure enr_stud(bno in students.B#%type, cid in classes.classid%type, clcount OUT number, stcount OUT number, clsize OUT number, cllimit OUT number, inclass OUT number, clenroll OUT number, precount OUT number, ispre OUT number, insem OUT number);
  procedure drop_stud(bno in students.B#%type, cid in enrollments.classid%type, clcount out number, stcount out number, clsize out number, clenroll out number, nclass out number, precount out number, insem out number);
  procedure drop_s(bno in students.B#%type,stcount out number);
  
end;
/

create or replace package body dbproject is

procedure sh_ta(ta_cursor OUT sys_refcursor) as
begin open ta_cursor for select * from tas;
end;

procedure sh_student(student_cursor OUT sys_refcursor) as
begin open student_cursor for select * from students;
end;

procedure sh_class(class_cursor OUT sys_refcursor) as
begin open class_cursor for select * from classes;
end;

procedure sh_course(course_cursor OUT sys_refcursor) as
begin open course_cursor for select * from courses;
end;

procedure sh_enrollment(enrollment_cursor OUT sys_refcursor) as
begin open enrollment_cursor for select * from enrollments;
end;

procedure sh_prerequisite(prerequisite_cursor OUT sys_refcursor) as
begin open prerequisite_cursor for select * from prerequisites;
end;

procedure sh_log(log_cursor OUT sys_refcursor) as
begin open log_cursor for select * from logs;
end;


procedure ta_classes(class_id IN classes.classid%type, ta_class_cursor OUT sys_refcursor, ret_message OUT varchar2) is
classid_check number(2);
ta_check number(2);
begin
select count(classid) into classid_check from classes where class_id=classid;
select count(classid) into ta_check from classes where class_id=classid and ta_B# IS NULL;
if classid_check = 0 then
ret_message := 'The classid is invalid';
else if ta_check = 0 then
open ta_class_cursor for
select s.B#,s.first_name,s.last_name
from classes c,students s
where s.B#=c.ta_B#
and c.classid=class_id;
ret_message := 'success';
else
ret_message := 'The class ha no TA';
end if;
end if;
end ta_classes;


procedure pre_course(dcode in prerequisites.dept_code%type, cno in prerequisites.course# %type, rc OUT sys_refcursor) as
begin open rc for select (pre_dept_code || pre_course#) Course from prerequisites
start with dept_code=dcode and course#=cno
connect by prior pre_dept_code = dept_code and prior pre_course#=course#;
end;


procedure enr_stud(bno in students.B#%type, cid in classes.classid%type, clcount OUT number, stcount OUT number, clsize OUT number, cllimit OUT number, inclass OUT number, clenroll OUT number, precount OUT number, ispre OUT number, insem OUT number) as
begin
  select count(classid) into clcount from classes where classid=cid;
  select count(B#) into stcount from students where B#=bno;
  select class_size into clsize from classes where classid=cid;
  select limit into cllimit from classes where classid=cid;
  select count(classid) into inclass from enrollments where classid=cid and B#=bno;
  select count(e.classid) into clenroll from enrollments e, classes c where e.classid=c.classid and bno=e.B# and (semester, year) in (select semester,year from classes where cid=classid);
  select count(B#) into precount from enrollments e,classes c where e.classid=c.classid and e.B#=bno and lgrade not in ('D','I') and (dept_code,course#) in (select pre_dept_code,pre_course# from classes c,prerequisites p where c.dept_code=p.dept_code and c.course#=p.course# and c.classid =cid);
  select count(*) into ispre from classes c,prerequisites p where c.dept_code=p.dept_code and c.course#=p.course# and cid=c.classid;
  select count(classid) into insem from classes where classid=cid and semester='Fall' and year='2018';
  
  if(clcount>0 and stcount>0) then
    if(cllimit>clsize) then
      if(inclass=0) then
        if(insem=0) then
          dbms_output.put_line('Cannot enroll into a class from a previous semester.');
        else
          if(ispre=0) then
            if(clenroll=4) then
              dbms_output.put_line('The student will be overloaded with the new enrollment.');
              insert into enrollments values (bno,cid,null);
            else if(clenroll >5) then
              dbms_output.put_line('The students cannot be enrolled in more than five classes in same semester.');
            else
              insert into enrollments values (bno,cid,null);
            end if;
            end if;
          else
            if(precount=0) then
              dbms_output.put_line('Prerequisite not satisfied.');
            else
              if(clenroll=4) then
                dbms_output.put_line('The student will be overloaded with the new enrollment.');
                insert into enrollments values (bno,cid,null);
              else if(clenroll >5) then
                dbms_output.put_line('The students cannot be enrolled in more than five classes in same semester.');
              else
                insert into enrollments values (bno,cid,null);
              end if;
              end if;
            end if;
          end if;
        end if;
      else
        dbms_output.put_line('The student is already in the class.');      
      end if;
    else
      dbms_output.put_line('The class is already full');
    end if;
  else
    if(clcount<1 and stcount<1) then
      dbms_output.put_line('The B# and classid are invalid.');
    else
      if(clcount>0 and stcount<1) then
        dbms_output.put_line('The B# is invalid');
      else if(clcount<1 and stcount>0) then
        dbms_output.put_line('The classid is invalid');
      end if;
      end if;
    end if;
  end if;
end enr_stud;

procedure drop_stud(bno in students.B#%type, cid in enrollments.classid%type, clcount out number, stcount out number, clsize out number, clenroll out number, nclass out number, precount out number, insem out number) as
begin
  select count(classid) into clcount from enrollments where classid=cid;
  select count(B#) into stcount from students where B#=bno;
  select class_size into clsize from classes where classid=cid;
  select count(*) into clenroll from enrollments where B#=bno and classid=cid;
  select count(classid) into precount from prerequisites p, classes c where p.pre_dept_code=c.dept_code and p.pre_course#=c.course# and cid=c.classid and (p.dept_code,p.course#) in (select dept_code,course# from enrollments e, classes c where e.classid = c.classid and e.B#=bno);
  select count(classid) into nclass from enrollments where B#=bno;
  select count(classid) into insem from classes where classid=cid and semester='Fall' and year='2018';
  
  if(clcount>0 and stcount>0) then
    if(nclass>0) then
      if(insem=0) then
        dbms_output.put_line('Only enrollment in the current semester can be dropped.');
      else
        if(precount=0) then
          if(nclass=1 and clsize=1) then
            delete from enrollments where B#=bno and cid=classid;
            dbms_output.put_line('This student is not enrolled in any classes.');
            dbms_output.put_line('The class now has no students.');
          else if(nclass=1) then
            delete from enrollments where B#=bno and cid=classid;
            dbms_output.put_line('This student is not enrolled in any classes.');
          else if(clsize=1) then
            delete from enrollments where B#=bno and cid=classid;
            dbms_output.put_line('The class now has no students.');
          else
            delete from enrollments where B#=bno and cid=classid;
          end if;
          end if;
          end if;
        else
          dbms_output.put_line('The drop is not permitted because another class the student registered uses it as a prerequisite.');
        end if;
      end if;
    else
      dbms_output.put_line('This student is not enrolled in any classes.');
    end if;
  else
    if(clcount<1 and stcount<1) then
      dbms_output.put_line('The B# and classid are invalid');
    else
      if(clcount>0 and stcount<1) then
        dbms_output.put_line('The B# is invalid.');
      else
        dbms_output.put_line('The classid is invalid.');
      end if;
    end if;
  end if;
end drop_stud;

procedure drop_s(bno in students.B#%type,stcount out number) is
begin
  select count(B#) into stcount from students where B#=bno;
  delete from students where B#=bno;
  if(stcount=0) then
    dbms_output.put_line('The B# is not valid.');
  end if;
end drop_s;

end;
/

drop trigger add_student_t;
create trigger add_student_t
  after insert on students
    for each row
  declare 
    uname varchar2(20);
    utime date;
  begin
    select user into uname from dual;
    select localtimestamp into utime from dual;
    insert into logs
    values (s_log.nextval,uname,utime,'students','insert',:new.B#);
  end;
/

drop trigger delete_student_t;
create trigger delete_student_t
  after delete on students
    for each row
  declare
    uname varchar2(20);
    utime date;
  begin
    select user into uname from dual;
    select localtimestamp into utime from dual;
    insert into logs
    values (s_log.nextval,uname,utime,'students','delete',:old.B#);
  end;
/ 

drop trigger add_enrollment_t;
create trigger add_enrollment_t
  after insert on enrollments
    for each row
  declare
    uname varchar2(20);
    utime date;
  begin
    select user into uname from dual;
    select localtimestamp into utime from dual;
    update classes set class_size=class_size+1 where classid=:new.classid;
    insert into logs
    values (s_log.nextval,uname,utime,'enrollments','insert',:new.B#);
  end;
/ 

drop trigger delete_enrollment_t;
create trigger delete_enrollment_t
  after delete on enrollments
    for each row
  declare
    uname varchar2(20);
    utime date;
  begin
    select user into uname from dual;
    select localtimestamp into utime from dual;
    update classes set class_size=class_size-1 where classid=:old.classid;
    insert into logs
    values (s_log.nextval,uname,utime,'enrollments','delete',:old.B#);
  end;
/

drop trigger en_stud_del;
create trigger en_stud_del
  after delete on students
    for each row
  begin
    delete from enrollments where enrollments.B#=:old.B#;
  end;
/

set serveroutput on;
show errors