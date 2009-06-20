/**
 * run this with ant sql task with delimiter="/" delimitertype="row"
 *keepformat="true"
 */
drop schema if exists clusterization cascade;
/

create schema clusterization;
/

set search_path = clusterization, pg_catalog;
/

/**
 * Node ifno for 'this' cluster.
 *Usually, this is only one record.
 **/
create table my_cluster_node_info
(
 node_id      INTEGER  not null,
 org_id       INTEGER  not null,
  primary key(node_id, org_id)
);
/

drop function if exists host_number(inet);
/


create or replace function host_number(inet) returns integer
as $$$$
declare
 sbytes text[];
 r      INTEGER;
begin 
 sbytes:=string_to_array(host($1),'.');
 r:=cast(sbytes[1] as INTEGER);
 r:=(r<<8)+cast(sbytes[2] as INTEGER);
 r:=(r<<8)+cast(sbytes[3] as INTEGER);
 r:=(r<<8)+cast(sbytes[4] as INTEGER);
 return r;
end $$$$ LANGUAGE plpgsql; 
/


insert into my_cluster_node_info(node_id, org_id) 
    values(host_number(inet_server_addr()),1);
/

create table  db_cluster_neightboards
(
 node_id  INTEGER  primary key,
 org_id   INTEGER 
);
/

create or replace function generate_number_key(BIGINT) returns NUMERIC(40,0)
as $$$$
declare
 cn INTEGER;
 vorg_id INTEGER;
 x  alias for $1;
 retval NUMERIC(40,0);
begin
 select node_id, org_id into cn, vorg_id
   from clusterization.my_cluster_node_info limit 1;
 if cn is null then
   raise exception 'clusterization metainfo (node_id) is not filled.';
 end if;
 if vorg_id is null then
   raise exception 'clusterization metainfo (org_id) is not filled.';
 end if;
 retval:=vorg_id*cast(2^32 as NUMERIC(40,0))+cn;
 retval:=retval*cast(2^64 as NUMERIC(40,0))+x;
 return retval;
end $$$$ LANGUAGE plpgsql;
/

create or replace function generate_character_key(BIGINT) returns CHAR(24)
as $$$$
declare
 cn INTEGER;
 vorg_id INTEGER;
 x  alias for $1;
 retval CHAR(24);
begin
 select node_id, org_id into cn, vorg_id
   from clusterization.my_cluster_node_info limit 1;
 if cn is null then
   raise exception 'clusterization metainfo(cn) is not filled.';
 end if;
 if vorg_id is null then
   raise exception 'clusterization metainfo(org_id) is not filled.';
 end if;
 retval:=substr(encode(
       (
         set_byte(E'a'::bytea,0,(vorg_id>>24)%256)::bytea
       ||set_byte(E'a'::bytea,0,(vorg_id>>16)%256)::bytea
       ||set_byte(E'a'::bytea,0,(vorg_id>>8)%256)::bytea
       ||set_byte(E'a'::bytea,0,(vorg_id%256))::bytea
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
             ,'base64'),0,23);
  return retval;
end $$$$ LANGUAGE plpgsql;
/


set search_path = public, pg_catalog;
/
