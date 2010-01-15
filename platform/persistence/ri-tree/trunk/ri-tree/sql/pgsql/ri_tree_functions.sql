

set search_path = ri_tree, pg_catalog;

--insert into ri_tree.ri_metadata(ri_structure_prefix, root, minstep)
--         values('ri_time',2^(63-1),1);

create or replace function ri_time_fork_node(bigint, bigint) returns bigint AS $$
declare
  lower ALIAS for $1;
  upper ALIAS for $2;
  node bigint;
  step bigint;
begin
  select root into node from ri_tree.ri_metadata where ri_structure_prefix='ri_time';
  if not found then
    raise exception 'ri_metadata for % is not found','ri_time';
  end if;
  step:=node/2;
  while step>=1 loop
   if node < lower then
     node:=node+step;
   elsif node > upper then
     node:=node-step;
   else
     return node;
   end if;
   step:=step/2;
  end loop;
  return node;
end;
$$ LANGUAGE plpgsql;

create or replace function insert_interval(in_lower bigint,
                                           in_upper bigint) returns void
AS
$$
declare
  fork bigint;
begin
  fork:=ri_tree.ri_time_fork_node(in_lower,in_upper);
  insert into ri_tree.ri_time_intervals(node,lower,upper)
                  values(fork,in_lower,in_upper);
  insert into ri_tree.ri_time_lower_upper_index_table(node,lower,upper)
                  values(fork,in_lower,in_upper);
  insert into ri_tree.ri_time_upper_lower_index_table(node,lower,upper)
                  values(fork,in_lower,in_upper);
end;
$$ LANGUAGE plpgsql;

create or replace function delete_interval(in_lower bigint,
                                           in_upper bigint) returns void
AS
$$
declare
  fork bigint;
begin
  fork:=ri_tree.ri_time_fork_node(in_lower,in_upper);
  delete from ri_tree.ri_time_upper_lower_index_table
           where node=fork and upper=in_upper and lower=in_lower;
  delete from ri_tree.ri_time_lower_index_table
           where node=fork and lower=in_lower and upper=in_upper;
  delete from ri_tree.ri_time_intervals
           where lower=in_lower and upper=in_upper;
end;
$$ LANGUAGE plpgsql;


drop type if exists ri_index_interval_type cascade;
create type ri_index_interval_type
AS
(
  node   bigint,
  lower  bigint,
  upper  bigint,
  isleft boolean,
  isright boolean,
  istop  boolean,
  isbottom boolean,
  isinner boolean
);


create or replace function ri_tree_query_intervals(
   lower bigint, upper bigint, 
   outTopLeft boolean, outTopRight boolean, outFork boolean,
   outBottomLeft boolean, outInnerLeft boolean,
   outInnerRight boolean, outBottomRight boolean,
   outLower boolean, outUpper boolean)
 returns setof ri_index_interval_type	
AS $$
DECLARE
  node  bigint;  
  step  bigint; 
  lstep bigint; 
  rstep bigint; 
  fork  bigint;
  xminstep INTEGER;
  retval ri_tree.ri_index_interval_type;
BEGIN
  select root, minstep 
     into node, xminstep 
     from ri_tree.ri_metadata where ri_structure_prefix='ri_time';
  if not found then
    raise exception 'ri_metadata for % is not found','ri_time';
  end if;
  step:=node/2;
  fork:=0;
  while step>= xminstep loop
    if node < lower then
      if outTopLeft then
        retval.node:=node;
        retval.upper:=node+step;
        retval.lower:=node-step;
        retval.isleft:=true;
        retval.isright:=false;
        retval.istop:=true;
        retval.isbottom:=false;
        retval.isinner:=false;
        return next retval;
      end if;
      node:=node+step;
    elsif node > upper then
      if outTopRight then
        retval.node=node;
        retval.upper:=node+step;
        retval.lower:=node-step;
        retval.isleft=false;
        retval.isright=true;
        retval.istop=true;
        retval.isbottom=false;
        retval.isinner=false;
        return next retval;
      end if;
      node:=node-step;
    else
      fork:=node;
      if outFork then
        retval.node=node;
        retval.upper:=node+step;
        retval.lower:=node-step;
        retval.isleft=false;
        retval.isright=false;
        retval.istop=false;
        retval.isbottom=false;
        retval.isinner=true;
        return next retval;
      end if;
      exit;  
    end if; 
    step:=step/2;
    if step < xminstep then
      if fork = 0 then
        fork = lower;
        if outFork then
          retval.node=node;
          retval.upper:=node;
          retval.lower:=node;
          retval.isleft=false;
          retval.isright=false;
          retval.istop=false;
          retval.isbottom=false;
          retval.isinner=true;
          return next retval;
        end if;
      end if;
    end if;
  end loop;
  if fork > lower then
    node:=fork - step;
    lstep:=step/2;
    while lstep >= xminstep loop
     if node < lower then
       if outBottomLeft then
        retval.node=node;
        retval.upper:=node+lstep;
        retval.lower:=node-lstep;
        retval.isleft=true;
        retval.isright=false;
        retval.istop=false;
        retval.isbottom=true;
        retval.isinner=false;
        return next retval;
       end if;
       node:=node+lstep;
     elsif node > lower then
       if outInnerLeft then
         retval.node=node;
         retval.upper:=node+lstep;
         retval.lower:=node-lstep;
         retval.isleft=true;
         retval.isright=false;
         retval.istop=false;
         retval.isbottom=false;
         retval.isinner=true;
         return next retval;
       end if;
       node:=node-lstep;
     else
       if outLower then
         retval.node=node;
         retval.upper:=node+lstep;
         retval.lower:=node-lstep;
         retval.isleft=true;
         retval.isright=false;
         retval.istop=false;
         retval.isbottom=false;
         retval.isinner=false;
         return next retval;
       end if;
       exit;
     end if; 
     lstep:=lstep/2;
    end loop; 
  elsif fork=lower then
   -- fork can be eq to lower and to upper
   if (outLower or outUpper) and not outFork then
      retval.node=fork;
      retval.upper:=fork+step;
      retval.lower:=fork-step;
      retval.isleft=false;
      retval.isright=false;
      retval.istop=false;
      retval.isbottom=false;
      retval.isinner=false;
      return next retval;
   end if;
  else
     -- impossible.
     raise exception 'fork < lower, impossible (%d,%d,%d)',fork,lower,upper; 
  end if;
  if fork < upper then
    node:=fork + step;
    rstep:=step/2;
    while rstep >= xminstep loop
      if node < upper then
        if outInnerRight then
          retval.node=node;
          retval.upper:=node+rstep;
          retval.lower:=node-rstep;
          retval.isleft=false;
          retval.isright=true;
          retval.istop=false;
          retval.isbottom=false;
          retval.isinner=true;
          return next retval;
        end if;
        node:=node+rstep;
      elsif node > upper then
        if outBottomRight then
          retval.node=node;
          retval.upper:=node+rstep;
          retval.lower:=node-rstep;
          retval.isleft=false;
          retval.isright=true;
          retval.istop=false;
          retval.isbottom=true;
          retval.isinner=false;
          return next retval;
        end if;
        node:=node-rstep;
      else
        -- node = upper
        if outUpper then
          retval.node=node; 
          retval.upper=node+rstep;
          retval.lower=node-rstep;
          retval.isleft=false;
          retval.isright=true;
          retval.istop=false;
          retval.isbottom=false;
          retval.isinner=false;
          return next retval;
        end if;
        exit;
      end if;
      rstep:=rstep/2;
    end loop;
  elsif fork=lower then
     -- do nothing
     null; 
  else
     raise exception 'fork > upper, impossible: (%d,%d,%d)', fork, lower, upper;
  end if;
END;
$$LANGUAGE plpgsql;

create or replace function ri_tree_query_top_left(
                                                 lower bigint, 
                                                 upper bigint
                                                 )
        returns setof ri_index_interval_type	
AS $$
BEGIN
 return query select * from ri_tree.ri_tree_query_intervals(
           lower, upper,
           true, false, false,
           false, false, false, false, false, false);
          
END;
$$ LANGUAGE plpgsql;

create or replace function ri_tree_query_top_right(
                                                 lower bigint, 
                                                 upper bigint
                                                 )
        returns setof ri_index_interval_type	
AS $$
BEGIN
 return query select * from ri_tree.ri_tree_query_intervals(
           lower, upper,
           false, true, false,
           false, false, false, false, false, false);
END;
$$ LANGUAGE plpgsql;


create or replace function ri_tree_query_bottom_left(
                                                 lower bigint, 
                                                 upper bigint
                                                 )
        returns setof ri_index_interval_type	
AS $$
BEGIN
 return query select * from ri_tree.ri_tree_query_intervals(
           lower, upper,
           false, false, false,
           true, false, false, false, false, false);
END;
$$ LANGUAGE plpgsql;


create or replace function ri_tree_query_all_left(
                                                 lower bigint, 
                                                 upper bigint
                                                 )
        returns setof ri_index_interval_type	
AS $$
BEGIN
 return query select * from ri_tree.ri_tree_query_intervals(
           lower, upper,
           true, false, false,
           true, false, false, false, false, false);
END;
$$ LANGUAGE plpgsql;

create or replace function ri_tree_query_all_right(
                                               lower bigint, 
                                               upper bigint
                                                 )
        returns setof ri_index_interval_type	
AS $$
BEGIN
 return query select * from ri_tree.ri_tree_query_intervals(
           lower, upper,
           /* top_left*/ false, /*top_rigth*/ true, /*fork*/ false,
           /* botttom-left*/ false, /*inner-left*/ false, 
           /* inner-ritht*/ false, /*bottom-right*/ true, 
           /* lower */ false, /* upper*/ false);
END;
$$ LANGUAGE plpgsql;


create or replace function ri_tree_query_top_right_fork(
                                                 lower bigint, 
                                                 upper bigint
                                                 )
        returns setof ri_index_interval_type	
AS $$
BEGIN
 return query select * from ri_tree.ri_tree_query_intervals(
           lower, upper, 
           /* top_left*/ false, /*top_rigth*/ true, /*fork*/ true,
           /* botttom-left*/ false, /*inner-left*/ false, 
           /* inner-ritht*/ false, /*bottom-right*/ false, 
           /* lower */ false, /* upper*/ false);
END;
$$ LANGUAGE plpgsql;

-- for meets.
create or replace function ri_tree_query_top_left_bottom_left_lower(
                                               lower bigint, 
                                               upper bigint
                                                 )
        returns setof ri_index_interval_type	
AS $$
BEGIN
 return query select * from ri_tree.ri_tree_query_intervals(
           lower, upper,  
           /* top_left*/ true, /*top_rigth*/ false, /*fork*/ false,
           /* botttom-left*/ true, /*inner-left*/ false, 
           /* inner-ritht*/ false, /*bottom-right*/ false, 
           /* lower */ true, /* upper*/ false);
END;
$$ LANGUAGE plpgsql;


-- for overlaps.
create or replace function ri_tree_query_top_left_bottom_left(
                                                 lower bigint, 
                                                 upper bigint
                                                 )
        returns setof ri_index_interval_type	
AS $$
BEGIN
 return query select * from ri_tree.ri_tree_query_intervals(
           lower, upper, 
           /* top_left*/ true, /*top_rigth*/ false, /*fork*/ false,
           /* botttom-left*/ true, /*inner-left*/ false, 
           /* inner-ritht*/ false, /*bottom-right*/ false, 
           /* lower */ false, /* upper*/ false);
END;
$$ LANGUAGE plpgsql;


-- for overlaps
create or replace function ri_tree_query_inner_left_lower_fork(
                                                 lower bigint, 
                                                 upper bigint
                                                 )
        returns setof ri_index_interval_type	
AS $$
BEGIN
 return query select * from ri_tree.ri_tree_query_intervals(
           lower, upper, 
           /* top_left*/ false, /*top_rigth*/ false, /*fork*/ true,
           /* botttom-left*/ false, /*inner-left*/ true, 
           /* inner-ritht*/ false, /*bottom-right*/ false, 
           /* lower */ true, /* upper*/ false);
END;
$$ LANGUAGE plpgsql;


-- for finishedBy
create or replace function ri_tree_query_top_left_fork(
                                               lower bigint, 
                                               upper bigint
                                                 )
        returns setof ri_index_interval_type	
AS $$
BEGIN
 return query select * from ri_tree.ri_tree_query_intervals(
           lower, upper, 
           /* top_left*/ true, /*top_rigth*/ false, /*fork*/ true,
           /* botttom-left*/ false, /*inner-left*/ false, 
           /* inner-ritht*/ false, /*bottom-right*/ false, 
           /* lower */ false, /* upper*/ false);
END;
$$ LANGUAGE plpgsql;

-- for starts
create or replace function ri_tree_query_inner_left_lower_fork(
                                               lower bigint, 
                                               upper bigint
                                                 )
        returns setof ri_index_interval_type	
AS $$
BEGIN
 return query select * from ri_tree.ri_tree_query_intervals(
           lower, upper, 
           /* top_left*/ false, /*top_rigth*/ false, /*fork*/ true,
           /* botttom-left*/ false, /*inner-left*/ true, 
           /* inner-right*/ false, /*bottom-right*/ false, 
           /* lower */ true, /* upper*/ false);
END;
$$ LANGUAGE plpgsql;

-- for during_eq
create or replace function ri_tree_query_lower_fork(
                                               lower bigint, 
                                               upper bigint
                                                 )
        returns setof ri_index_interval_type	
AS $$
BEGIN
 return query select * from ri_tree.ri_tree_query_intervals(
           lower, upper, 
           /* top_left*/ false, /*top_rigth*/ false, /*fork*/ true,
           /* botttom-left*/ false, /*inner-left*/  false, 
           /* inner-right*/ false, /*bottom-right*/ false, 
           /* lower */ true, /* upper*/ false);
END;
$$ LANGUAGE plpgsql;

-- for during_eq
create or replace function ri_tree_query_inner_right_upper_fork(
                                               lower bigint, 
                                               upper bigint
                                                 )
        returns setof ri_index_interval_type	
AS $$
BEGIN
 return query select * from ri_tree.ri_tree_query_intervals(
           lower, upper, 
           /* top_left*/ false, /*top_rigth*/ false, /*fork*/ true,
           /* botttom-left*/ false, /*inner-left*/  false, 
           /* inner-right*/ true, /*bottom-right*/ false, 
           /* lower */ false, /* upper*/ true);
END;
$$ LANGUAGE plpgsql;



--------
-------- select queries
--------
drop type if exists ri_tree_interval_lw_type cascade;
create type ri_tree_interval_lw_type as
(
 lower bigint,
 upper bigint
);

create or replace function ri_time_intervals_before(
                                   lower bigint,
                                   upper bigint
                                         )
        returns setof ri_tree_interval_lw_type
AS $$
BEGIN
 return query select i.lower, i.upper from ri_tree.ri_time_intervals i
    where i.node < lower and i.upper < lower;
END;
$$ LANGUAGE plpgsql;

create or replace function ri_time_intervals_before(
                                    lower TIMESTAMP WITH TIME ZONE, 
                                    upper TIMESTAMP WITH TIME ZONE
                                         )
        returns setof ri_tree_interval_lw_type
AS $$
BEGIN
  return query select * from ri_tree.ri_time_intervals_before(
                     cast(extract(epoch from lower) as bigint),  
                     cast(extract(epoch from upper) as bigint)
                                           );
END;
$$ LANGUAGE plpgsql;

create or replace function ri_time_intervals_after(
                                   lower bigint,
                                   upper bigint
                                         )
        returns setof ri_tree_interval_lw_type
AS $$
BEGIN
 return query select i.lower, i.upper from ri_tree.ri_time_intervals i
    where i.node > upper and  i.lower > upper;
END;
$$ LANGUAGE plpgsql;

create or replace function ri_time_intervals_contains(
                                           lower bigint,
                                           upper bigint
                                         )
        returns setof ri_tree_interval_lw_type
AS $$
BEGIN
 return query (
   select i.lower, i.upper from ri_tree.ri_time_intervals i,
                            ri_tree.ri_tree_query_top_right_fork(lower,upper) q
                       where
                            i.node = q.node 
                           and 
                            i.lower < lower
                           and
                            i.upper > upper
     union all
   select i.lower, i.upper from ri_tree.ri_time_intervals i,
                            ri_tree.ri_tree_query_top_left(lower,upper) q
                       where
                            i.node = q.node 
                           and 
                            upper < i.upper
  );
END;
$$ LANGUAGE plpgsql;


create or replace function ri_time_intervals_contains(
                                    lower TIMESTAMP WITH TIME ZONE, 
                                    upper TIMESTAMP WITH TIME ZONE
                                         )
        returns setof ri_tree_interval_lw_type
AS $$
BEGIN
  return query select * from ri_tree.ri_time_intervals_contains(
                     cast(extract(epoch from lower) as bigint),  
                     cast(extract(epoch from upper) as bigint)
                                           );
END;
$$ LANGUAGE plpgsql;


create or replace function ri_time_intervals_during(
                                           lower bigint,
                                           upper bigint
                                         )
        returns setof ri_tree_interval_lw_type
AS $$
declare
  fork  bigint;
BEGIN
 fork:=ri_tree.ri_time_fork_node(lower,upper);
 return query (
   select i.lower, i.upper from ri_tree.ri_time_intervals i
                       where
                            i.node <= fork
                           and 
                            i.lower > lower
                           and
                            i.upper < upper
     union all
   select i.lower, i.upper from ri_tree.ri_time_intervals i
                       where
                            i.node > fork 
                           and 
                            i.node < upper
                           and
                            i.upper < upper
  );
END;
$$ LANGUAGE plpgsql;

create or replace function ri_time_intervals_during(
                                    lower TIMESTAMP WITH TIME ZONE, 
                                    upper TIMESTAMP WITH TIME ZONE
                                         )
        returns setof ri_tree_interval_lw_type
AS $$
BEGIN
  return query select * from ri_tree.ri_time_intervals_during(
                     cast(extract(epoch from lower) as bigint),  
                     cast(extract(epoch from upper) as bigint)
                                           );
END;
$$ LANGUAGE plpgsql;


create or replace function ri_time_intervals_contains_eq(
                                           lower bigint,
                                           upper bigint
                                         )
        returns setof ri_tree_interval_lw_type
AS $$
BEGIN
 return query (
   select i.lower, i.upper from ri_tree.ri_time_intervals i,
                            ri_tree.ri_tree_query_top_right_fork(lower,upper) q
                       where
                            i.node = q.node 
                           and 
                            i.lower <= lower
                           and
                            i.upper => upper
     union all
   select i.lower, i.upper from ri_tree.ri_time_intervals i,
                            ri_tree.ri_tree_query_top_left(lower,upper) q
                       where
                            i.node = q.node 
                           and 
                            upper <= i.upper
  );
END;
$$ LANGUAGE plpgsql;

create or replace function ri_time_intervals_contains_eq(
                                    lower TIMESTAMP WITH TIME ZONE, 
                                    upper TIMESTAMP WITH TIME ZONE
                                         )
        returns setof ri_tree_interval_lw_type
AS $$
BEGIN
  return query select * from ri_tree.ri_time_intervals_contains_eq(
                     cast(extract(epoch from lower) as bigint),  
                     cast(extract(epoch from upper) as bigint)
                                           );
END;
$$ LANGUAGE plpgsql;



create or replace function ri_time_intervals_during_eq(
                                           lower bigint,
                                           upper bigint
                                         )
        returns setof ri_tree_interval_lw_type
AS $$
declare
  fork  bigint;
BEGIN
 fork:=ri_tree.ri_time_fork_node(lower,upper);
 return query (
   select i.lower, i.upper from ri_tree.ri_time_intervals i
                       where
                            i.node <= fork
                           and 
                            i.lower >= lower
                           and
                            i.upper <= upper
     union all
   select i.lower, i.upper from ri_tree.ri_time_intervals i
                       where
                            i.node > fork 
                           and 
                            i.node < upper
                           and
                            i.upper <= upper
  );
END;
$$ LANGUAGE plpgsql;


create or replace function ri_time_intervals_meets(
                                           lower bigint,
                                           upper bigint
                                         )
        returns setof ri_tree_interval_lw_type
AS $$
declare
  fork  bigint;
BEGIN
 fork:=ri_tree.ri_time_fork_node(lower,upper);
 return query (
   select i.lower, i.upper 
            from ri_tree.ri_time_intervals i,
                 ri_tree.ri_tree_query_top_left_bottom_left_lower q
                       where
                            i.node = q.node
                           and 
                            i.upper = lower
  );
END;
$$ LANGUAGE plpgsql;


create or replace function ri_time_intervals_intersect(
                                           lower bigint,
                                           upper bigint
                                         )
        returns setof ri_tree_interval_lw_type
AS $$
declare
  fork  bigint;
BEGIN
 return query (
   select i.lower, i.upper 
            from ri_tree.ri_time_intervals i,
                 ri_tree.ri_tree_query_all_left q
             where
                   i.node = q.node
                  and
                   i.upper >= lower
    union all
   select i.lower, i.upper 
            from ri_tree.ri_time_intervals i,
                 ri_tree.ri_tree_query_all_right q
             where
                   i.node = q.node
                 and
                   i.lower <= upper
    union all
   select i.lower, i.upper 
            from ri_tree.ri_time_intervals i
            where i.node between lower and upper
 );
END;
$$ LANGUAGE plpgsql;


create or replace function ri_time_intervals_equals(
                                           lower bigint,
                                           upper bigint
                                         )
        returns setof ri_tree_interval_lw_type
AS $$
declare
  fork  bigint;
BEGIN
 fork:=ri_tree.ri_time_fork_node(lower,upper);
 return query (
   select i.lower, i.upper 
            from ri_tree.ri_time_intervals i
             where
               i.node = fork
             and 
               i.lower = lower
             and 
               i.upper = upper
  );
END;
$$ LANGUAGE plpgsql;



/**
 * ri_time_intervals_contains_or_started_by
 **/
create or replace function ri_time_intervals_contains_strtby(
                                           lower bigint,
                                           upper bigint
                                         )
        returns setof ri_tree_interval_lw_type
AS $$
BEGIN
 return query (
   select i.lower, i.upper from ri_tree.ri_time_intervals i,
                            ri_tree.ri_tree_query_top_right_fork(lower,upper) q
                       where
                            i.node = q.node 
                           and 
                            i.lower <= lower
                           and
                            i.upper > upper
     union all
   select i.lower, i.upper from ri_tree.ri_time_intervals i,
                            ri_tree.ri_tree_query_top_left(lower,upper) q
                       where
                            i.node = q.node 
                           and 
                            upper < i.upper
  );
END;
$$ LANGUAGE plpgsql;
