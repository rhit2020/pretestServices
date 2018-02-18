import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DB extends dbInterface {
    public DB(String connurl, String user, String pass) {
        super(connurl, user, pass);
    }

	public Double getPrevAttemptResult(String usr, String grp, String prevAttempt) {

		try {
			Double res = null;
			stmt = conn.createStatement();

			String query = " SELECT result FROM um2.ent_user_activity UA" + 
						   " where UA.userid = (select userid from um2.ent_user where login='"+ usr + "')" +
						   " and UA.groupid = (select userid from um2.ent_user where login='"+ grp + "')"+
					       " and UA.activityid = (select activityid from um2.ent_activity where activity='"+ prevAttempt + "')"; 
			rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				res = rs.getDouble("result");
			}
			return res; //returns the last result (value of last record)
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return null;
		} finally {
			this.releaseStatement(stmt, rs);
		}
	}
	
	public List<String> getPastAttempts(String usr, String grp, String[] questions) {

		try {
			List<String >list = new ArrayList<String>();
			stmt = conn.createStatement();
			String questionsTxt = getQuestionsTxt(questions);
			
			String query = " SELECT distinct A.activity FROM um2.ent_user_activity UA, " + 
						   " ent_activity A" +
						   " where UA.userid = (select userid from um2.ent_user where login='"+ usr + "')" +
						   " and UA.groupid = (select userid from um2.ent_user where login='"+ grp + "')"+
					       " and UA.activityid in (select activityid from um2.ent_activity where activity in " +
						   " ("+ questionsTxt + ")) and A.activityid = UA.activityid;";

			rs = stmt.executeQuery(query);
			while (rs.next()) {
				list.add(rs.getString("activity"));
			}
			return list; //returns the last result (value of last record)
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return null;
		} finally {
			this.releaseStatement(stmt, rs);
		}
	}
	
	private static String getQuestionsTxt(String[] activityList) {
		if (activityList == null)
			return "";
		String activities = "";
		for (String a : activityList)
			activities += "'" + a + "',";
		if (activities.length() > 0)
			activities = activities.substring(0, activities.length() - 1); // this is
																		// for
																		// ignoring
																		// the
																		// last
																		// ,
		return activities;
	}

	public String getCourseContent(int cid) {
		try {
			String contents = "";
			stmt = conn.createStatement();

			String query = " SELECT  C.content_name " +
                           " FROM ent_topic T, rel_topic_content TC, ent_content C" +
                           " WHERE T.course_id = " + cid + 
                           " and T.active=1 and C.visible = 1 " +
                           " and TC.visible = 1 " +
                           " and TC.topic_id=T.topic_id and C.content_id = TC.content_id;"; 
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				contents += rs.getString("content_name") + ",";
			}
			if (contents.length() > 0)
				contents = contents.substring(0, contents.length() - 1); // this is
																			// for
																			// ignoring
																			// the
																			// last
																			// ,
			return contents; //returns the last result (value of last record)
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return null;
		} finally {
			this.releaseStatement(stmt, rs);
		}
	}

}
