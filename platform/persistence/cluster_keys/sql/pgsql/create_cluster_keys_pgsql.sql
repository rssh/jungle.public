
drop schema clusterization cascade;
create schema clusterization;

set search_path = clusterization, pg_catalog;

/**
 * Node ifno for 'this' cluster.
 *Usually, this is only one record.
 **/
create table my_cluster_node_info
(
 node_id      INTEGER  not null,
 org_id       INTEGER  not null
  primary key(node_number, org_id)
);

insert into my_cluster_node_info(node_id, org_id) values(1,1);

create table  db_cluster_neightboards
(
 node_id  INTEGER  primary key,
 org_id   INTEGER 
);

create or replace function generate_number_key(BIGINT) returns NUMERIC(40,0)
as $$
declare
 cn INTEGER;
 org_id INTEGER;
 x  alias for $1;
 retval NUMERIC(40,0);
begin
 select node_id, org_id into cn, org_id
   from clusterization.my_cluster_node_info limit 1;
 if cn is null then
   raise exception 'clusterization metainfo is not filled.';
 end if;
 retval:=org_id*cast(2^32 as NUMERIC(40,0))+cn;     
 retval:=retval*cast(2^64 as NUMERIC(40,0))+x;
 return retval;
end $$ LANGUAGE plpgsql;

create or replace function generate_character_key(BIGINT) returns CHAR(24)
declare
as $$
 cn INTEGER;
 org_id INTEGER;
 x  alias for $1;
 retval CHAR(24);
begin
 select node_id, org_id into cn, org_id
   from clusterization.my_cluster_info limit 1;
 if cn is null then
   raise exception 'clusterization metainfo is not filled.';
 end if;
 retval:=encode(
       (
         set_byte(E'a'::bytea,0,(org_id>>24)%256)::bytea
       ||set_byte(E'a'::bytea,0,(org_id>>16)%256)::bytea
       ||set_byte(E'a'::bytea,0,(org_id>>8)%256)::bytea
       ||set_byte(E'a'::bytea,0,(org_id%256))::bytea
       ||set_byte(E'a'::bytea,0,(cn>>24)%256)::bytea
       ||set_byte(E'a'::bytea,0,(cn>>16)%256)::bytea
       ||set_byte(E'a'::bytea,0,(cn>>8)%256)::bytea
       ||set_byte(E'a'::bytea,0,(cn%256))::bytea
       ||set_byte(E'a'::bytea,0,cast((x>>56)%256 as integer))::bytea
       ||set_byte(E'a'::bytea,0,cast((x>>48)%256 as integer))::bytea
       ||set_byte(E'a'::bytea,0,cast((x>>40)%256 as integer))::bytea
       ||set_byte(E'a'::bytea,0,cast((x>>32)%256 as integer))::bytea
       ||set_byte(E'a'::bytea,0,cast((x>>24)%256 as integer))::bytea
       ||set_byte(E'a'::bytea,0,cast((x>>16)%256 as integer))::bytea
       ||set_byte(E'a'::bytea,0,cast((x>>8)%256 as integer))::bytea
       ||set_byte(E'a'::bytea,0,cast(x%256 as integer))::bytea
       )
             ,'base64');
  return retval;
end $$ LANGUAGE plpgsql;


