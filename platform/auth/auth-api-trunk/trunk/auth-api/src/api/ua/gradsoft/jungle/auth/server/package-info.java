/**
 * This package define server-side model of authorization.
 *Main entities are:
 *<p>
 *Server API: pluggable server must implement 
 *  <code> AuthServerApiProvider </code> for using auth model.
 *<p>
 * Example of implementation for client API backend:
 *                  <code> AuthClientApiHttpRequestScopeImpl </code>
 *<p>
 * Set of annotations, to mark server-side classes. General idea is the
 * same as with javax.security but permission-based instead role-based.
 * <ul>
 *   <li> Require </li>
 *   <li> Permission </li>
 *   <li> RequirePermission </li>
 * </ul>
 **/ 
package ua.gradsoft.jungle.auth.server;

