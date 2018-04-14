

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class ProcessPretestAnswer
 */
@WebServlet("/ProcessAttempt")
public class ProcessAttempt extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/*---------------------------------------------------------
	 * The course-contents relationship data structure (STATIC)
	 * --------------------------------------------------------- */			
	private static HashMap<String, String> course_contents;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProcessAttempt() {
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
		String cid = request.getParameter("cid"); // course id
		String prevAttempt = request.getParameter("prevAttempt"); // name of the previous attempt
		String event = request.getParameter("event"); // event 
		String updatesm = request.getParameter("updatesm"); // updatesm 

		/*---------------------------------------------------------
		 * Getting Data that we need for computation of student progress
		 * --------------------------------------------------------- */			
		if (ProcessAttempt.course_contents == null)
			course_contents = new HashMap<String, String>() ; //fill the static variable the first time
		
		String contents = null;
		if (cid != null) {
			contents  = course_contents.get(cid);
			if (contents == null) {
				try {
					contents = getCourseContents(Integer.parseInt(cid)); 
					if (contents != null)
						course_contents.put(cid, contents);
				} catch (Exception e) {
					System.out.println("AggregateUMServices/ProcessPretestAnswer: cid could not be parsed to a valid integer.");}
			}
		} 
		Double result = getPrevAttemptResult(usr, grp, prevAttempt);
		//if the prev result is not null, send an asynchronous call to update BN beliefs
		boolean update = true;
		if (updatesm != null)
			update = (updatesm.equals("no") == false);
		if (update && result != null && contents != null && contents.isEmpty() == false) {
			String params = createParamJSON(usr, grp, prevAttempt, result, contents, event);
			HttpAsyncClientInterface.getInstance().sendHttpAsynchPostRequest(params);
		}
		
		//return the response to the user
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String output = "{\"name\":\""+prevAttempt+"\",\"attempted\":\""+ (result == null ? "0" : "1") + "\"}";
		out.print(output);
		
	}
	
	private String getCourseContents(int cid) {
		DB agg_db;
		ConfigManager cm = new ConfigManager(this);
		agg_db = new DB(cm.agg_dbstring,cm.agg_dbuser,cm.agg_dbpass);
		agg_db.openConnection();
		String contents = agg_db.getCourseContent(cid);
		agg_db.closeConnection();
		return contents;
	}


	private Double getPrevAttemptResult(String usr, String grp, String prevAttempt) {
		DB um2_db;
		ConfigManager cm = new ConfigManager(this);
		um2_db = new DB(cm.um2_dbstring,cm.um2_dbuser,cm.um2_dbpass);
		um2_db.openConnection();
		Double result = um2_db.getPrevAttemptResult(usr, grp, prevAttempt);
		um2_db.closeConnection();
		return result;
	}


	private String createParamJSON(String usr, String grp, String lastAct, 
			Double lastActResult, String contents,
			String event) {
		JSONObject json = new JSONObject();
		json.put("usr", usr);
		json.put("grp", grp);
		json.put("lastContentId", lastAct);
		json.put("lastContentResult", "" + lastActResult);
		json.put("contents", contents);
		json.put("event", event);
		return json.toString();
	}
	

}
