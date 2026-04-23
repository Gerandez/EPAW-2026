package epaw.lab1;

import epaw.lab1.util.DBManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/hello")
public class HelloWorld extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Vibescrolling</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 0; background: #f7f7f5; color: #1f2933; }");
        out.println("main { max-width: 760px; margin: 0 auto; padding: 48px 20px; }");
        out.println("h1 { margin: 0; font-size: 2rem; font-weight: 700; letter-spacing: -0.03em; }");
        out.println("h2 { margin: 0 0 18px; font-size: 1.15rem; font-weight: 700; }");
        out.println(".subtitle { margin: 8px 0 32px; color: #667085; line-height: 1.5; }");
        out.println(".card { background: #ffffff; border: 1px solid #e5e7eb; border-radius: 12px; padding: 24px; margin-bottom: 18px; }");
        out.println("table { width: 100%; border-collapse: collapse; }");
        out.println("th, td { padding: 12px 0; text-align: left; border-bottom: 1px solid #edf0f2; vertical-align: top; }");
        out.println("th { color: #667085; font-size: 0.78rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.06em; }");
        out.println("tr:last-child td { border-bottom: 0; }");
        out.println("form { display: grid; gap: 14px; }");
        out.println("label { font-size: 0.9rem; font-weight: 700; display: block; margin-bottom: 6px; }");
        out.println("input, textarea, select { width: 100%; box-sizing: border-box; border: 1px solid #d0d5dd; border-radius: 8px; padding: 10px 12px; font: inherit; background: white; color: #1f2933; }");
        out.println("input:focus, textarea:focus, select:focus { outline: 2px solid #dbeafe; border-color: #2563eb; }");
        out.println("textarea { min-height: 96px; resize: vertical; }");
        out.println("button { background: #111827; color: white; border: 0; border-radius: 8px; padding: 10px 14px; font: inherit; font-weight: 700; cursor: pointer; width: fit-content; }");
        out.println("button:hover { background: #374151; }");
        out.println(".muted { color: #667085; margin: 14px 0 0; font-size: 0.95rem; }");
        out.println(".empty { text-align: center; color: #667085; padding: 16px 0; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<main>");
        out.println("<h1>Vibescrolling</h1>");
        out.println("<p class='subtitle'>Create an agent profile, store it in SQLite, and show it below.</p>");
        out.println("<div class='card'>");
        out.println("<h2>Current feed</h2>");
        out.println("<table>");
        out.println("<tr><th>ID</th><th>Agent Name</th><th>Profile</th></tr>");

        boolean hasRows = false;

        try (DBManager db = new DBManager()) {
            PreparedStatement stmt = db.prepareStatement("SELECT id, name, description FROM users");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                hasRows = true;
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");

                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + name + "</td>");
                out.println("<td>" + description + "</td>");
                out.println("</tr>");
            }
        } catch (Exception e) {
            out.println("<tr><td colspan='3'>Error: " + e.getMessage() + "</td></tr>");
            e.printStackTrace();
        }

        if (!hasRows) {
            out.println("<tr><td colspan='3' class='empty'>No users found</td></tr>");
        }

        out.println("</table>");
        out.println("</div>");

        out.println("<div class='card'>");
        out.println("<h2>Register agent</h2>");
        out.println("<form method='POST' action='" + request.getContextPath() + "/hello'>");
        out.println("<div><label for='name'>Agent Name</label><input id='name' name='name' type='text' maxlength='20' required></div>");
        out.println("<div><label for='role'>Agent Role</label><select id='role' name='role' required><option value=''>Select role...</option><option value='Chatbot'>Chatbot</option><option value='Image Generator'>Image Generator</option><option value='Voice Assistant'>Voice Assistant</option><option value='Rebel AI'>Rebel AI</option></select></div>");
        out.println("<div><label for='description'>Tagline</label><textarea id='description' name='description' maxlength='255' required></textarea></div>");
        out.println("<button type='submit'>Create Agent</button>");
        out.println("</form>");
        out.println("<p class='muted'>Vibescrolling saves a new row in SQLite and reloads the feed automatically.</p>");
        out.println("</div>");

        out.println("</main>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String role = request.getParameter("role");
        String description = request.getParameter("description");

        if (name != null) {
            name = name.trim();
        }
        if (role != null) {
            role = role.trim();
        }
        if (description != null) {
            description = description.trim();
        }

        if (name != null && !name.isEmpty() && role != null && !role.isEmpty()
                && description != null && !description.isEmpty()) {
            try (DBManager db = new DBManager()) {
                PreparedStatement stmt = db.prepareStatement("INSERT INTO users (name, description) VALUES (?, ?)");
                stmt.setString(1, name);
                stmt.setString(2, "Role: " + role + " | " + description);
                stmt.executeUpdate();
            } catch (Exception e) {
                throw new ServletException("Unable to save user", e);
            }
        }

        response.sendRedirect(request.getContextPath() + "/hello");
    }
}
