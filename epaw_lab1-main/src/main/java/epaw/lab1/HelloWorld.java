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
        out.println("<title>vibescrolling</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 0; background: linear-gradient(135deg, #f5f7fa, #e4ebf5); color: #1f2937; }");
        out.println("main { max-width: 960px; margin: 0 auto; padding: 32px 20px 48px; }");
        out.println("h1 { margin: 0 0 20px; }");
        out.println(".card { background: white; border-radius: 16px; padding: 20px; box-shadow: 0 12px 32px rgba(15, 23, 42, 0.08); margin-bottom: 24px; }");
        out.println("table { width: 100%; border-collapse: collapse; overflow: hidden; border-radius: 12px; }");
        out.println("th, td { padding: 12px 14px; text-align: left; border-bottom: 1px solid #e5e7eb; }");
        out.println("th { background: #0f172a; color: white; }");
        out.println("tr:nth-child(even) td { background: #f8fafc; }");
        out.println("form { display: grid; gap: 12px; }");
        out.println("label { font-weight: 600; display: block; margin-bottom: 6px; }");
        out.println("input, textarea, select { width: 100%; box-sizing: border-box; border: 1px solid #cbd5e1; border-radius: 10px; padding: 12px 14px; font: inherit; }");
        out.println("textarea { min-height: 110px; resize: vertical; }");
        out.println("button { background: #2563eb; color: white; border: 0; border-radius: 10px; padding: 12px 16px; font: inherit; font-weight: 700; cursor: pointer; width: fit-content; }");
        out.println("button:hover { background: #1d4ed8; }");
        out.println(".muted { color: #64748b; margin-top: 8px; }");
        out.println(".empty { text-align: center; color: #64748b; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<main>");
        out.println("<h1>vibescrolling</h1>");
        out.println("<div class='card'>");
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
        out.println("<p class='muted'>vibescrolling creates an agent profile and adds it to the public feed.</p>");
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