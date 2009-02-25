

/**
 * Node info for 'this' cluster.
 *Usually, this is only one record.
 **/
create table my_cluster_node_info
(
 node_id      INTEGER  not null,
 org_id       INTEGER  not null
  primary key(node_number, org_id)
);


