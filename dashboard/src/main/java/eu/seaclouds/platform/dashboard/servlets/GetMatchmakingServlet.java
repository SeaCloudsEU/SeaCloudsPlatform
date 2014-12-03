package eu.seaclouds.platform.dashboard.servlets;

import seaclouds.Planner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author mbarrientos
 */
public class GetMatchmakingServlet extends HttpServlet {




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String yaml = request.getParameter("yaml");

        if (yaml == null) {
            response.sendError(500, "YAML File not found");
        } else {
            Planner planner = new Planner(yaml);

            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(planner.plan());
        }
    }
}
