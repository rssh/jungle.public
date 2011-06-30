

/**
 * Node info for 'this' cluster.
 *Usually, this is only one record.
 **/
create table my_cluster_node_info
(
 node_id      NUMBER(10,0)  not null,
 org_id       NUMBER(10,0)  not null
  primary key(node_number, org_id)
);


