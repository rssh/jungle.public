
drop schema clusterization cascade;
create schema clusterization;

set search_path = clusterization, pg_catalog;

-- this table must have only one record.
create table my_cluster_info
(
 my_cluster_number  INTEGER  not null,
 my_org_id          INTEGER  not null,
 my_key             INTEGER  not null
);

insert into my_cluster_info(my_cluster_number, my_org_id, my_key) values(1,1,1);

create table  db_cluster_neightboards
(
 cluster_number  INTEGER  primary key,
 org_id          INTEGER 
);

create table db_replicated_tables
(
 schema_name varchar(64),
 table_name  varchar(128),
 generate    boolean,
 accept      boolean,
 primary key(schema_name, table_name)
);

create or replace function generate_number_key(BIGINT) returns NUMERIC(40,0)
as $$
declare
 cn INTEGER;
 org_id INTEGER;
 x  alias for $1;
 retval NUMERIC(40,0);
begin
 select my_cluster_number, my_org_id into cn, org_id
   from clusterization.my_cluster_info;
 if cn is null then
   raise exception 'clusterization metainfo is not filled.';
 end if;
 retval:=org_id*cast(2^32 as NUMERIC(40,0))+cn;     
 retval:=retval*cast(2^64 as NUMERIC(40,0))+x;
 return retval;
end $$ LANGUAGE plpgsql;



