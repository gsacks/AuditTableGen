set search_path to testSandbox

--create table table1 (table1id serial, data character(255),  PRIMARY KEY (table1id) )

select * from table1

insert into table1 (data) values ('test1')

--create role testuser with LOGIN PASSWORD 'testuserpw'

select * from information_schema.enabled_roles

grant all on schema testsandbox to testuser
grant all privileges on all tables in schema testsandbox to testuser
ALTER DEFAULT PRIVILEGES IN SCHEMA testsandbox GRANT SELECT ON TABLES TO PUBLIC;
ALTER DEFAULT PRIVILEGES IN SCHEMA testsandbox GRANT ALL ON TABLES TO testuser;