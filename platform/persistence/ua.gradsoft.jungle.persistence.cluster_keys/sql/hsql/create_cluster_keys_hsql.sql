
drop table my_cluster_node_info if exists;

create table my_cluster_node_info
(
 node_id      INTEGER  not null,
 org_id       INTEGER  not null,
  primary key(node_id, org_id)
);


