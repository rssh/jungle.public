

set search_path = ri_tree, pg_catalog;

create or replace function ri_tree_query_intervals(
   lower bigint, upper bigint, 
   outTopLeft boolean, outTopRight boolean, outFork boolean,
   outBottomLeft boolean, outInnerLeft boolean,
   outInnerRight boolean, outBottomRight boolean)
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
        retval.isleft=false;
        retval.isright=true;
        retval.istop=true;
        retval.isbottom=false;
        retval.isinner=false;
        return next retval;
      end if;
      node:=node-step;
    else
      if outFork then
        retval.node=node;
        retval.isleft=false;
        retval.isright=false;
        retval.istop=false;
        retval.isbottom=false;
        retval.isinner=true;
        return next retval;
      end if;
      fork:=node;
      exit;  
    end if; 
    step:=step/2;
  end loop;
  if fork > lower then
    node:=fork - step;
    lstep:=step/2;
    while lstep >= xminstep loop
     if node < lower then
       if outBottomLeft then
        retval.node=node;
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
         retval.isleft=true;
         retval.isright=false;
         retval.istop=false;
         retval.isbottom=false;
         retval.isinner=true;
         return next retval;
       end if;
       node:=node-lstep;
     else
       exit;
     end if; 
     lstep:=lstep/2;
    end loop; 
  end if;
  if fork < upper then
    node:=fork + step;
    rstep:=step/2;
    while rstep >= xminstep loop
      if node < upper then
        if outInnerRight then
          retval.node=node;
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
       exit;
      end if;
      rstep:=rstep/2;
    end loop;
  end if;
END;
$$LANGUAGE plpgsql;

create or replace function ri_tree_query_intervals(
             lower TIMESTAMP, upper TIMESTAMP,
             outTopLeft boolean, outTopRight boolean, outFork boolean,
             outBottomLeft boolean, outInnerLeft boolean,
             outInnerRight boolean, outBottomRight boolean)
        returns setof ri_index_interval_type	
AS $$
BEGIN
 return query select * from ri_tree.ri_tree_query_intervals(
           cast(extract(epoch from lower) as bigint),  
           cast(extract(epoch from upper) as bigint),
           outTopLeft, outTopRight, outFork,
           outBottomLeft, outInnerLeft,
           outInnerRight, outBottomRight);
END;
$$ LANGUAGE plpgsql;

