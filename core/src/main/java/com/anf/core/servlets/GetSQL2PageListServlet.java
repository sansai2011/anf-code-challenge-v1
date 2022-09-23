package com.anf.core.servlets;

import com.anf.core.services.GetPageList;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.  annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

@Component(service = { Servlet.class })
@SlingServletPaths(
        value="/bin/sql")
@ServiceDescription("Simple Demo Servlet")
public class GetSQL2PageListServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    GetPageList getPageListService;

    @Override
    protected void doGet(final SlingHttpServletRequest request,
                         final SlingHttpServletResponse response) throws ServletException, IOException {

        List<String> results = getPageListService.getTopTenPageListUsingSQL2(request);
        response.setContentType("text/plain");
        Gson gson = new Gson();
        response.getWriter().println(gson.toJson(results));
    }
}
