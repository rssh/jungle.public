
create language plpgsql;

create schema ri_tree;

drop table if exists ri_metadata;
drop table if exists ri_time_intervals;
drop index if exists ri_time_lower_upper_index;
drop index if exists ri_time_upper_lower_index;
drop type if exists ri_time_std_intervals cascade;

set search_path = ri_tree, pg_catalog;

drop table if exists ri_metadata;

create table ri_metadata
(
  ri_structure_prefix varchar(20) primary key,
  root      int8,
  minstep   integer,
  left_root int8,
  right_root int8
);

-- 40 is up to year 19391 with 1-sec. resolution
insert into ri_tree.ri_metadata(ri_structure_prefix, root, minstep)
  values('ri_time',2^(40-1),1);


drop table if exists ri_time_intervals cascade;

create table ri_tree.ri_time_intervals
(
 node  bigint,
 lower bigint,
 upper bigint,
 primary key(lower,upper)
);

drop sequence if exists ri_tree_id_seq;
create sequence ri_tree_id_seq;


drop index if exists ri_time_node_lower_upper_index;
drop index if exists ri_time_lower_upper_index;
drop index if exists ri_time_node_upper_lower_index;
drop index if exists ri_time_upper_lower_index;
create index ri_time_node_lower_upper_index 
                on ri_time_intervals(node,lower,upper);
create index ri_time_lower_upper_index 
                on ri_time_intervals(lower,upper);
create index ri_time_node_upper_lower_index 
                on ri_time_intervals(node,upper,lower);
create index ri_time_upper_lower_index
                on ri_time_intervals(upper,lower);

drop table if exists ri_time_lower_upper_index_table;
create table ri_time_lower_upper_index_table
(
 node  bigint,
 lower bigint,
 upper bigint,
 primary key(node,lower,upper),
 foreign key(lower,upper) references ri_time_intervals(lower,upper)
);

create index idx_luit_nl on ri_time_lower_upper_index_table (node,lower) ;
create index idx_luit_lu on ri_time_lower_upper_index_table (lower,upper) ;

drop table if exists ri_time_upper_lower_index_table;
create table ri_time_upper_lower_index_table
(
 node  bigint,
 upper bigint,
 lower bigint,
 primary key(node,upper,lower),
 foreign key(lower,upper) references ri_time_intervals(lower,upper)
);


create index idx_ulit_nu on ri_time_lower_upper_index_table (node,upper) ;
create index idx_ulit_ul on ri_time_lower_upper_index_table (upper,lower) ;

