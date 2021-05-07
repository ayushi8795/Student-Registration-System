import java.sql.*;
import java.util.*;
import oracle.jdbc.*;
import java.math.*;
import java.io.*;
import java.awt.*;
import oracle.jdbc.pool.OracleDataSource;
//import sun.security.util.Password;

public class dbproject {

	static Scanner sc = new Scanner(System.in);
	static dbproject db = new dbproject();

	public static void main(String[] args) throws SQLException {
		try{
			System.out.println("Enter username");					
			String usern = sc.nextLine();
			System.out.println("Enter password");
			//char pass[] = Password.readPassword(System.in);
			String passn = sc.nextLine();
			//String passn= new String(pass);
			OracleDataSource ds = new oracle.jdbc.pool.OracleDataSource();
      		ds.setURL("jdbc:oracle:thin:@castor.cc.binghamton.edu:1521:acad111");
      		Connection conn = ds.getConnection(usern, passn);
      		System.out.println("Connection successfull");
    		db.main_menu(conn);
      		//Start
      		/*Statement stmt = conn.createStatement ();

     		// Save result
        	ResultSet rset;
        	rset = stmt.executeQuery ("SELECT * FROM tas");

      		// Print
      		while (rset.next ()) {
         		System.out.print (rset.getString (1)+"  ");
         		System.out.print (rset.getString (2)+"  ");
         		System.out.println (rset.getString (3)+"  ");
         		//System.out.print (rset.getString (4)+"  ");
         		//System.out.print (rset.getString (5)+"  ");
         		//System.out.println (rset.getString (6)+"  ");
      		}
      		//End*/
    		conn.close();
		}

		catch(SQLException ex){
			System.out.println("SQLException caught : " + ex);
		}
	}


	void main_menu(Connection conn) throws SQLException{
		int select=0;
		do{
			System.out.println("1. Display tables");
			System.out.println("2. Show ta details of a particular classid");
			System.out.println("3. Prerequisite course");
			System.out.println("4. Enroll a student");
			System.out.println("5. Drop a student from a class");
			System.out.println("6. Delete a student record");
			System.out.println("7. Exit");

			select = sc.nextInt();

			switch(select){
				case 1:db.display(conn);
					break;
				case 2:ta_classes(conn);
					break;
				case 3:pre_course(conn);
					break;
				case 4:enroll_stud(conn);
					break;
				case 5:delete_stud(conn);
					break;
				case 6:drop_student(conn);
					break;
				case 7:System.exit(1);
				default:
					System.out.println("Option does not exists");
			}
		}while (select!=7);
	} 

	void display(Connection conn) throws SQLException{
		System.out.println("1. tas");
		System.out.println("2. students");
		System.out.println("3. classes");
		System.out.println("4. courses");
		System.out.println("5. enrollments");
		System.out.println("6. prerequisites");
		System.out.println("7. logs");

		int select=sc.nextInt();

		switch(select){
			case 1:db.display_tas(conn);
				break;
			case 2:db.display_students(conn);
				break;
			case 3:db.display_classes(conn);
				break;
			case 4:db.display_courses(conn);
				break;
			case 5:db.display_enrollments(conn);
				break;
			case 6:db.display_prerequisites(conn);
				break;
			case 7:db.display_logs(conn);
				break;
			default:db.main_menu(conn);
		}
	}

	void display_tas(Connection conn) throws SQLException{
	
		//Prepare to call stored procedure:
        CallableStatement cs = conn.prepareCall("begin dbproject.sh_ta(?); end;");

        //register the out parameter (the first parameter)
        cs.registerOutParameter(1, OracleTypes.CURSOR);


        // execute and retrieve the result set
        cs.execute();
        ResultSet rs = ((OracleCallableStatement)cs).getCursor(1);
        
        // print the results

        System.out.println("B#\tta_level\toffice");
        
        while (rs.next()) {
            System.out.print(rs.getString(1) + "\t");
            System.out.print(rs.getString(2) + "\t \t");
            System.out.println(rs.getString(3) + "\t");
        }
        rs.close();
        cs.close();
	}

	void display_students(Connection conn) throws SQLException{
		CallableStatement cs = conn.prepareCall("begin dbproject.sh_student(?); end;");
		cs.registerOutParameter(1,OracleTypes.CURSOR);
		cs.execute();
		ResultSet rs = ((OracleCallableStatement)cs).getCursor(1);
		System.out.println("B#\tfname\tlname\tstatus\tgpa\temail\tbdate\tdept_name");
		while(rs.next()){
			System.out.print(rs.getString(1) + "\t");
			System.out.print(rs.getString(2) + "\t \t");
			System.out.print(rs.getString(3) + "\t");
			System.out.print(rs.getString(4) + "\t");
			System.out.print(rs.getString(5) + "\t");
			System.out.print(rs.getString(6) + "\t");
			System.out.print(rs.getString(7) + "\t");
			System.out.println(rs.getString(8) + "\t");
		}
		rs.close();
		cs.close();
	}

	void display_classes(Connection conn) throws SQLException{
		CallableStatement cs = conn.prepareCall("begin dbproject.sh_class(?); end;");
		cs.registerOutParameter(1,OracleTypes.CURSOR);
		cs.execute();
		ResultSet rs = ((OracleCallableStatement)cs).getCursor(1);
		System.out.println("classid\tdeptcode\tcourse#\tsec#\tyear\tsemester\tlimit\tclasssize\troom\tta_B#");
		while(rs.next()){
			System.out.print(rs.getString(1) + "\t");
			System.out.print(rs.getString(2) + "\t");
			System.out.print(rs.getString(3) + "\t");
			System.out.print(rs.getString(4) + "\t");
			System.out.print(rs.getString(5) + "\t");
			System.out.print(rs.getString(6) + "\t");
			System.out.print(rs.getString(7) + "\t");
			System.out.print(rs.getString(8) + "\t");
			System.out.print(rs.getString(9) + "\t");
			System.out.println(rs.getString(10) + "\t");
		}
		rs.close();
		cs.close();
	}

	void display_courses(Connection conn) throws SQLException{
		CallableStatement cs = conn.prepareCall("begin dbproject.sh_course(?); end;");
		cs.registerOutParameter(1,OracleTypes.CURSOR);
		cs.execute();
		ResultSet rs = ((OracleCallableStatement)cs).getCursor(1);
		System.out.println("dept_code\tcourse#\ttitle");
		while(rs.next()){
			System.out.print(rs.getString(1) + "\t");
			System.out.print(rs.getString(2) + "\t");
			System.out.println(rs.getString(3) + "\t");
		}
		rs.close();
		cs.close();
	}

	void display_enrollments(Connection conn) throws SQLException{
		CallableStatement cs = conn.prepareCall("begin dbproject.sh_enrollment(?); end;");
		cs.registerOutParameter(1,OracleTypes.CURSOR);
		cs.execute();
		ResultSet rs = ((OracleCallableStatement)cs).getCursor(1);
		System.out.println("B#\tclassid\tlgrade");
		while(rs.next()){
			System.out.print(rs.getString(1) + "\t");
			System.out.print(rs.getString(2) + "\t");
			System.out.println(rs.getString(3) + "\t");
		}
		rs.close();
		cs.close();
	}

	void display_prerequisites(Connection conn) throws SQLException{
		CallableStatement cs = conn.prepareCall("begin dbproject.sh_prerequisite(?); end;");
		cs.registerOutParameter(1,OracleTypes.CURSOR);
		cs.execute();
		ResultSet rs = ((OracleCallableStatement)cs).getCursor(1);
		System.out.println("dept_code\tcourse#\tpredeptcode\tprecourse#");
		while(rs.next()){
			System.out.print(rs.getString(1) + "\t");
			System.out.print(rs.getString(2) + "\t");
			System.out.print(rs.getString(3) + "\t");
			System.out.println(rs.getString(4) + "\t");
		}
		rs.close();
		cs.close();
	}

	void display_logs(Connection conn) throws SQLException{
		CallableStatement cs = conn.prepareCall("begin dbproject.sh_log(?); end;");
		cs.registerOutParameter(1,OracleTypes.CURSOR);
		cs.execute();
		ResultSet rs = ((OracleCallableStatement)cs).getCursor(1);
		System.out.println("log#\topname\toptime\ttablename\toperation\tkeyvalue");
		while(rs.next()){
			System.out.print(rs.getString(1) + "\t");
			System.out.print(rs.getString(2) + "\t");
			System.out.print(rs.getString(3) + "\t");
			System.out.print(rs.getString(4) + "\t");
			System.out.print(rs.getString(5) + "\t");
			System.out.println(rs.getString(6) + "\t");
		}
		rs.close();
		cs.close();
	}

	void ta_classes(Connection conn) throws SQLException{
		System.out.println("Enter classid:");
		String classid=sc.next();
		CallableStatement cs = conn.prepareCall("begin dbproject.ta_classes(?,?,?); end;");
		cs.setString(1,classid);
		cs.registerOutParameter(2,OracleTypes.CURSOR);
		cs.registerOutParameter(3,OracleTypes.VARCHAR);
		cs.execute();
		ResultSet rs;
		String msg = cs.getString(3);
		if(msg.equals("success")){
			rs = (ResultSet)cs.getObject(2);
			System.out.println("B#\tfname\tlname");
			while(rs.next()){
				System.out.print(rs.getString(1) + "\t");
				System.out.print(rs.getString(2) + "\t");
				System.out.println(rs.getString(3) + "\t");
			}
			rs.close();
		}
		else {
			System.out.println(msg);
		}
		
		cs.close();
	}

	void pre_course(Connection conn) throws SQLException{
		System.out.println("Enter course department:");
		String course_dept = sc.next();
		System.out.println("Enter course number:");
		String course_no = sc.next();
		CallableStatement cs = conn.prepareCall("begin dbproject.pre_course(?,?,?); end;");
		cs.setString(1,course_dept);
		cs.setString(2,course_no);
		cs.registerOutParameter(3,OracleTypes.CURSOR);
		cs.execute();
		ResultSet rs=(ResultSet)cs.getObject(3);
		System.out.println("deptcode\tcourse#");
		while(rs.next()){
			System.out.println(rs.getString(1) + "\t");
			//System.out.println(rs.getString(2) + "\t");
		}
		rs.close();
		cs.close();
	}

	void enroll_stud(Connection conn) throws SQLException{
		System.out.println("Enter B#:");
		String bno = sc.next();
		System.out.println("Enter classid:");
		String cid = sc.next();
		CallableStatement cs = conn.prepareCall("begin dbproject.enr_stud(?,?,?,?,?,?,?,?,?,?,?); end;");
		cs.setString(1,bno);
		cs.setString(2,cid);
		cs.registerOutParameter(3,OracleTypes.NUMBER);
		cs.registerOutParameter(4,OracleTypes.NUMBER);
		cs.registerOutParameter(5,OracleTypes.NUMBER);
		cs.registerOutParameter(6,OracleTypes.NUMBER);
		cs.registerOutParameter(7,OracleTypes.NUMBER);
		cs.registerOutParameter(8,OracleTypes.NUMBER);
		cs.registerOutParameter(9,OracleTypes.NUMBER);
		cs.registerOutParameter(10,OracleTypes.NUMBER);
		cs.registerOutParameter(11,OracleTypes.NUMBER);
		cs.execute();
		Object clcount = cs.getObject(3);
		Object stcount = cs.getObject(4);
		Object clsize = cs.getObject(5);
		Object cllimit = cs.getObject(6);
		Object inclass = cs.getObject(7);
		Object clenroll = cs.getObject(8);
		Object precount = cs.getObject(9);
		Object ispre = cs.getObject(10);
		Object insem = cs.getObject(11);

		int clcountI = Integer.parseInt(clcount.toString());
		int stcountI = Integer.parseInt(stcount.toString());
		int clsizeI = Integer.parseInt(clsize.toString());
		int cllimitI = Integer.parseInt(cllimit.toString());
		int inclassI = Integer.parseInt(inclass.toString());
		int clenrollI = Integer.parseInt(clenroll.toString());
		int precountI = Integer.parseInt(precount.toString());
		int ispreI = Integer.parseInt(ispre.toString());
		int insemI = Integer.parseInt(insem.toString());


		if(clcountI>0 && stcountI>0)
		{
			if(cllimitI>clsizeI)
			{
				if(inclassI==0)
				{
					if(insemI==0) 
					{
						System.out.println("Cannot enroll into a class from a previous semester.");
					}
					else
					{
						if(ispreI==0) 
						{
							if(clenrollI==4)
							{
								System.out.println("The student will be overloaded with the new enrollment.");
							}
							else if(clenrollI>5)
							{
								System.out.println("The students cannot be enrolled in more than five classes in same semester.");
							}
						}
						else
						{
							if(precountI==0)
							{
								System.out.println("prerequisites not satisfied.");
							}
							else
							{
								if(clenrollI==4)
								{
									System.out.println("The student will be overloaded with the new enrollment.");
								}
								else if(clenrollI>5)
								{
									System.out.println("The students cannot be enrolled in more than five classes in same semester.");
								}
							}
						}
					}
				}
				else
				{
					System.out.println("The student is already in the class.");
				}
			}
			else
			{
				System.out.println("The class is already full.");
			}
		}
		else
		{
			if(clcountI<1 && stcountI<1)
			{
				System.out.println("The B# and classid are invalid.");
			}
			else
			{
				if(clcountI>0 && stcountI<1) 
				{
					System.out.println("The B# is invalid.");
				}
				else if(clcountI<1 && stcountI>0)
				{
					System.out.println("The classid is invalid.");
				}
			}
		}
	}

	void delete_stud(Connection conn) throws SQLException{
		System.out.println("Enter B#:");
		String bno = sc.next();
		System.out.println("Enter classid:");
		String cid = sc.next();
		CallableStatement cs = conn.prepareCall("begin dbproject.drop_stud(?,?,?,?,?,?,?,?,?); end;");
		cs.setString(1,bno);
		cs.setString(2,cid);
		cs.registerOutParameter(3,OracleTypes.NUMBER);
		cs.registerOutParameter(4,OracleTypes.NUMBER);
		cs.registerOutParameter(5,OracleTypes.NUMBER);
		cs.registerOutParameter(6,OracleTypes.NUMBER);
		cs.registerOutParameter(7,OracleTypes.NUMBER);
		cs.registerOutParameter(8,OracleTypes.NUMBER);
		cs.registerOutParameter(9,OracleTypes.NUMBER);
		cs.execute();
		Object clcount = cs.getObject(3);
		Object stcount = cs.getObject(4);
		Object clsize = cs.getObject(5);
		Object clenroll = cs.getObject(6);
		Object nclass = cs.getObject(7);
		Object precount = cs.getObject(8);
		Object insem = cs.getObject(9);

		int clcountI = Integer.parseInt(clcount.toString());
		int stcountI = Integer.parseInt(stcount.toString());
		int clsizeI = Integer.parseInt(clsize.toString());
		int clenrollI = Integer.parseInt(clenroll.toString());
		int nclassI = Integer.parseInt(nclass.toString());
		int precountI = Integer.parseInt(precount.toString());
		int insemI = Integer.parseInt(insem.toString());

		if(clcountI>0 && stcountI>0)
		{
			if(nclassI>0) 
			{
				if(insemI==0) 
				{
					System.out.println("Only enrollment in the current semester can be dropped.");
				}
				else
				{
					if(precountI==0)
					{
						if(nclassI==1 && clsizeI==1) 
						{
							System.out.println("This student is not enrolled in any classes.");
							System.out.println("The class now has no students.");
						}
						else if(nclassI==1) 
						{
							System.out.println("This student is not enrolled in any classes.");
						}
						else if(clsizeI==1) 
						{
							System.out.println("The class now has no students.");
						}
					}
					else
					{
						System.out.println("The drop is not permitted because another class the student registered uses it as a prerequisite.");
					}
				}
			}
			else
			{
				System.out.println("This student is not enrolled in any classes.");
			}
		}	
		else
		{
			if(clcountI<1 && stcountI<1) 
			{
				System.out.println("The B# and classid are invalid.");
			}
			else
			{
				if(clcountI>0 && stcountI<1)
				{
					System.out.println("The B# is invalid");
				}
				else
				{
					System.out.println("The classid is invalid.");
				}
			}
		}	
	}

	void drop_student(Connection conn) throws SQLException{
		System.out.println("Enter B#:");
		String bno = sc.next();
		CallableStatement cs = conn.prepareCall("begin dbproject.drop_s(?,?); end;");
		cs.setString(1,bno);
		cs.registerOutParameter(2,OracleTypes.NUMBER);
		cs.execute();
		Object stcount = cs.getObject(2);
		int stcountI = Integer.parseInt(stcount.toString());
		if(stcountI==0)
		{
			System.out.println("The B# is invalid.");
		}
	}


}


