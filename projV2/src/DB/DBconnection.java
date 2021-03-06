package DB;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class DBconnection {

    private static final String USERNAME = "dbuser";
    private static final String PASSWORD = "dbpassword";
    private static final String CONNURL = "jdbc:mysql://localhost:3306";
    private static final String DBURL = "jdbc:mysql://localhost:3306/SE_proj";
    private static final String DBname = "SE_proj";


    public static void CreateAndConnect () throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        String sqlCreatePriorityTypes = "CREATE TABLE `SE_proj`.`priority_type` ( `id` INT(30) NOT NULL AUTO_INCREMENT , `name` VARCHAR(30) NOT NULL , PRIMARY KEY (`id`))";
        String sqlCreateProjects = "CREATE TABLE `SE_proj`.`projects` ( `id` INT(30) NOT NULL AUTO_INCREMENT , `name` VARCHAR(30) NOT NULL , `priority_id` INT(30) NOT NULL , `user_id` INT(30) NOT NULL , PRIMARY KEY (`id`))";
        String sqlCreateSkillTypes = "CREATE TABLE `SE_proj`.`skil_types` ( `id` INT(30) NOT NULL AUTO_INCREMENT , `skill` VARCHAR(30) NOT NULL , PRIMARY KEY (`id`))";
        String sqlCreateSubTasks = "CREATE TABLE `SE_proj`.`sub_tasks` ( `id` INT(30) NOT NULL AUTO_INCREMENT , `sub_task` VARCHAR(300) NOT NULL , `priority_id` INT NOT NULL , `user_id` INT NOT NULL ,  `task_id` INT NOT NULL , PRIMARY KEY (`id`))";
        String sqlCreateTasks = "CREATE TABLE `SE_proj`.`tasks` ( `id` INT(30) NOT NULL AUTO_INCREMENT , `task` VARCHAR(300) NOT NULL , `priority_id` INT NOT NULL , `user_id` INT NOT NULL , `skill_id` INT NOT NULL , `project_id` INT NOT NULL , PRIMARY KEY (`id`))";
        String sqlCreateUserAuth = "CREATE TABLE `SE_proj`.`user_auth` ( `id` INT(30) NOT NULL AUTO_INCREMENT , `user_name` VARCHAR(30) NOT NULL , `password` VARCHAR(30) NOT NULL , `user_id` INT(30) NOT NULL , PRIMARY KEY (`id`))";
        String sqlCreateUsers = "CREATE TABLE `SE_proj`.`users` ( `id` INT(30) NOT NULL AUTO_INCREMENT , `first_name` VARCHAR(30) NOT NULL , `sure_name` VARCHAR(30) NOT NULL , `user_name` VARCHAR(30) NOT NULL , `email` VARCHAR(50) NOT NULL , `password` VARCHAR(50) NOT NULL,  `is_admin` BOOLEAN NOT NULL , PRIMARY KEY (`id`))";

        boolean flag = true;
        ResultSet rs = null;
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conMain = DriverManager.getConnection(CONNURL,USERNAME,PASSWORD);
        Connection conDB = null;
        Statement stmtDB = null;
        rs = conMain.getMetaData().getCatalogs();

        while(rs.next()){
            if (DBname.equals(rs.getString(1)))
                flag = false;
        }

        if (flag == true) {
            Statement stmt = conMain.createStatement();
            stmt.executeUpdate("CREATE DATABASE " + DBname);
            conDB = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);
            stmtDB = conDB.createStatement();
            stmtDB.executeUpdate(sqlCreatePriorityTypes);
            stmtDB.executeUpdate(sqlCreateProjects);
            stmtDB.executeUpdate(sqlCreateSkillTypes);
            stmtDB.executeUpdate(sqlCreateSubTasks);
            stmtDB.executeUpdate(sqlCreateTasks);
            stmtDB.executeUpdate(sqlCreateUserAuth);
            stmtDB.executeUpdate(sqlCreateUsers);
        }
    }

    public static ResultSet getUsersList () throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection con = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);
        Statement stmt=con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users");
        return rs;
    }

    public static boolean checkIfUserExists(String username) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException{
        ResultSet rs = getUsersList();
        while (rs.next())
            if (rs.getString("user_name").equals(username))
                return true;
        return false;
    }

    public static boolean createUser (String username, String password, boolean isAdmin, String firstName, String sureName, String email) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection con = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);
        Statement stmt=con.createStatement();
        int numOfColEffected = 0;
        if (!checkIfUserExists(username))
            numOfColEffected = stmt.executeUpdate("INSERT INTO 'users' ('first_name', 'sure_name', 'user_name', 'email', 'password', 'is_admin') VALUES (\""+firstName+"\",\""+sureName+"\",\""+username+"\",\""+email+"\",\""+password+"\",\""+isAdmin+"\")");
        if (numOfColEffected != 0)
            return true;
        return false;
    }

    public static boolean checkLogin (String userName, String password) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException{
        ResultSet rs = getUsersList();
        while (rs.next())
            if (rs.getString("user_name").equals(userName) && rs.getString("password").equals(password))
                return true;
        return false;
    }

    public static boolean addTask (String userID, String task, int priorityID, int skillID, int projectID) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException{
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection con = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);
        Statement stmt=con.createStatement();
        int numOfColEffected = 0;
        numOfColEffected = stmt.executeUpdate("INSERT INTO 'tasks' ('task','priority_id','user_id','skill_id','project_id') VALUES (\""+task+"\",\""+priorityID+"\",\""+userID+"\",\""+skillID+"\",\""+projectID+"\")");
        if (numOfColEffected != 0)
            return true;
        return false;
    }


    public static ResultSet getTask () throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException{
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection con = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);
        Statement stmt=con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM tasks");
        return rs;
    }

    public static List getTaskByProject (int projectID) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        ResultSet rs = getTask();
        List tasksLst = new ArrayList(); // Create an ArrayList object
        while (rs.next())
            if (rs.getString("project_id").equals(projectID))
                tasksLst.add(rs.getString("task"));
        return tasksLst;
    }

    public static boolean addSubTask (String subTask, int priorityID, int userID, int taskID) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException{
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection con = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);
        Statement stmt=con.createStatement();
        int numOfColEffected = 0;
        numOfColEffected = stmt.executeUpdate("INSERT INTO 'sub_tasks' ('sub_task','priority_id','user_id','task_id') VALUES (\""+subTask+"\",\""+priorityID+"\",\""+userID+"\",\""+taskID+"\")");
        if (numOfColEffected != 0)
            return true;
        return false;
    }

    public static boolean getSubTask (String userID, String task, int priorityID, int skillID, int projectID) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException{
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection con = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);
        Statement stmt=con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT  * FROM sub_tasks");
        return rs;
    }
}



