

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class ProcessPretestAnswer
 */
@WebServlet("/GetPastAttempts")
public class GetPastAttempts extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPastAttempts() {
        super();
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//get the URL parameters
		String usr = request.getParameter("usr"); // user name
		String grp = request.getParameter("grp"); // group name
		String questions = request.getParameter("questions"); // course id
		
		String[] questionsArr = null;
		if (questions != null) {
			questionsArr = questions.split(",");
		}
		
		String message = null;
		if ( questionsArr != null ) {
			List<String> attemptedQ = getPastAttempts(usr, grp, questionsArr);
			JSONArray ja = new JSONArray();
			JSONObject jo;
			// adding kc estimates
			for (String q : questionsArr) {
				jo = new JSONObject();
				jo.put("name", q);
				jo.put("attempted", (attemptedQ.contains(q)? "1" : "0"));
				ja.put(jo);
			}
			message = ja.toString();
		}
		//return the response to the user
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(message == null?"[]":message);
	}

	private List<String> getPastAttempts(String usr, String grp, String[] questions) {
		DB um2_db;
		ConfigManager cm = new ConfigManager(this);
		um2_db = new DB(cm.um2_dbstring,cm.um2_dbuser,cm.um2_dbpass);
		um2_db.openConnection();
		List<String> attemptedQuestions = um2_db.getPastAttempts(usr, grp, questions);
		um2_db.closeConnection();
		return attemptedQuestions;
	}	

}
