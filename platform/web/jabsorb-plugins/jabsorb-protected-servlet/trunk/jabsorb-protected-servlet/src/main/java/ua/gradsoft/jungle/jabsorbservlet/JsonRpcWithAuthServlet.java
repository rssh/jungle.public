package ua.gradsoft.jungle.jabsorbservlet;

import javax.servlet.http.HttpServletRequest;
import org.jabsorb.JSONRPCBridge;
import org.jabsorb.JSONRPCServlet;


public class JsonRpcWithAuthServlet extends JSONRPCServlet
{


    @Override
    protected JSONRPCBridge findBridge(HttpServletRequest request) {
        return new JsonRpcAuthProxyBridge(super.findBridge(request));
    }

}

