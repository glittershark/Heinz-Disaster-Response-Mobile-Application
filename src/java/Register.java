
import com.mysql.jdbc.Connection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import DataDAO.*;
import javax.servlet.RequestDispatcher;

/**
 *
 * @author Xue Zhang
 */
public class Register extends HttpServlet {

    private static Connection con;
    private String user = null;
    private String pw_con = null;

    /*Get the database user and password from config file*/
    @Override
    public void init() throws ServletException {

        user = getInitParameter("dbUser");
        pw_con = getInitParameter("dbPassword");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }
        String connectionStr = "jdbc:mysql://localhost/DisasterAssessment";
        try {
            con = (Connection) DriverManager.getConnection(connectionStr, user, pw_con);
            String firstName = request.getParameter("volunteer_firstname");
            String lastName = request.getParameter("volunteer_lastname");
            String email_input = request.getParameter("volunteer_email");
            String email = email_input.toLowerCase();
            String password = request.getParameter("volunteer_password");
            String passwordConfirm = request.getParameter("volunteer_password1");
            User Client = null;
            if (password.equals(passwordConfirm) && email != null) {
                String registerMessage = null;
                String destination = null;
                if (User.lookup(email, con) != null) {
                    registerMessage = "Sorry! The e-mail address " + email_input + " has already been registered. Please try another one.";
                    destination = "/register.jsp";
                } else {

                    Client = new User(password, lastName, firstName, "", "", email);

                    int flag = Client.Insert(con);

                    if (flag == 1) {
                        registerMessage = "Congratulations! You have been registered successfully! Your account is currently pending approval. Please ask your supervisor to activate your account, then log in below.";
                        destination = "/index.jsp";
                    } else {
                        registerMessage = "Sorry, we were unable to register you. Please make sure that you've completed all fields below.";
                        destination = "/register.jsp";
                    }
                }
                request.setAttribute("RegisterMessage", registerMessage);
                RequestDispatcher red = getServletContext().getRequestDispatcher(destination);
                red.forward(request, response);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(con!=null)
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }
}
