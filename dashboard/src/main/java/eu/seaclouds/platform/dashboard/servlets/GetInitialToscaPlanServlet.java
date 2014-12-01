package eu.seaclouds.platform.dashboard.servlets;

import com.google.api.client.util.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author mbarrientos
 */
public class GetInitialToscaPlanServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        InputStream input = getClass().getResourceAsStream("/nuroCase.yaml");
        if (input == null) {
            response.sendError(500, "YAML File not found");
        } else {
            String nuroCaseYaml = CharStreams.toString(new InputStreamReader(input, Charsets.UTF_8));
            Closeables.closeQuietly(input);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(nuroCaseYaml);
        }
    }
}
