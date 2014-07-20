package reasonary;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse rsp)
    throws ServletException, IOException {
    rsp.getWriter().println("{'yo':'dawg'}");
  }
}
