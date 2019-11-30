import com.mongodb.*;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBHandler {
    static MongoClient mongoClient;

    static {
        try {
            mongoClient = new MongoClient();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    static DB database = mongoClient.getDB("lsh");
    static DBCollection users = database.getCollection("users");
    static DBCollection courses = database.getCollection(("courses"));

    static void signUp(String login, String password){
        DBObject query = new BasicDBObject("username",login);
        DBCursor cursor = users.find(query);
        if(cursor.hasNext()){
            throw new RuntimeException("Username already taken");
        }
        String hashed = BCrypt.hashpw(password,BCrypt.gensalt(12));
        users.insert(new BasicDBObject().append("username",login).append("password",hashed).append("editableCourses", Arrays.asList()).append("courses", Arrays.asList()));
    }

    static String login(String login, String password){
        DBObject query = new BasicDBObject("username",login);
        DBCursor cursor = users.find(query);
        if(!cursor.hasNext()){
            throw new RuntimeException("Invalid username or wrong password");
        }
        String hashedP= cursor.one().get("password").toString();
        if(!BCrypt.checkpw(password,hashedP)){
            throw new RuntimeException("Invalid username or wrong password");
        }
        return login;
    }


    static List<String> getFiles(String login){
        List<String> ret = new ArrayList<String>();
        List<BasicDBObject> userCourses = getCoursesObject(login,"courses");
        for(BasicDBObject course : userCourses){
            ret.addAll(getFilesStr(course));
        }
        return ret;
    }

    static List<String> getFilesInCourse(String login, String course){
        List<String> userCourses=getCourses(login);
        if(userCourses.contains(course)){
            List<String> ret = new ArrayList<String>();
            DBObject query = new BasicDBObject("name",course);
            DBCursor cursor = courses.find(query);
            List<String> files = (List<String>) cursor.one().get("files");
            for(String file: files){
                String f = file;
                ret.add(f);
            }
            return ret;
        }
        else{
            return null;
        }
    }

    static List<String> getCourses(String login){
        List<String> ret = new ArrayList<String>();
        List<BasicDBObject> userCourses = getCoursesObject(login, "courses");
        for(BasicDBObject course : userCourses){
            ret.add(getCourseNameFromId(course));
        }
        return ret;
    }

    static List<String> getEditableCourses(String login){ //redundancy kept for the sake of readability: getCourses(...), getEditableCourses(...) vs. getCourses(..., boolean editable)
        List<String> ret = new ArrayList<String>();
        List<BasicDBObject> userCourses = getCoursesObject(login, "editableCourses");
        for(BasicDBObject course : userCourses){
            ret.add(getCourseNameFromId(course));
        }
        return ret;
    }

    static void createCourse(String login, String courseName){
        DBObject query = new BasicDBObject("username",login);
        DBObject query1 = new BasicDBObject("name",courseName);
        DBCursor cursor1 = DBHandler.courses.find(query1);
        if(cursor1.hasNext()){
            throw new RuntimeException("Course name already taken");
        }
        DBObject course = new BasicDBObject().append("name",courseName).append("files", Arrays.asList());
        courses.insert(course);
        DBObject updateQuery = new BasicDBObject("$push",new BasicDBObject("courses", new BasicDBObject("id",course.get("_id"))));
        users.update(query,updateQuery);
        updateQuery = new BasicDBObject("$push",new BasicDBObject("editableCourses", new BasicDBObject("id",course.get("_id"))));
        users.update(query,updateQuery);
    }

    static void addFile(String login, String coursename,String filename){
        if(!getEditableCourses(login).contains(coursename)){
            throw new RuntimeException("You don't own a course with that name");
        }
        BasicDBObject course_=getCourse(login,coursename);
        List<String> filesAlready = getFilesStr(course_);
        if(filesAlready.contains(filename)){
            throw new RuntimeException("There is already a file with that name in the selcted course");
        }
        DBObject updateQuery = new BasicDBObject("$push",new BasicDBObject("files", filename));
        DBObject query = new BasicDBObject("name",coursename);
        courses.update(query,updateQuery);
    }

    static void addListener(String login, String listener, String course){
        if(!getEditableCourses(login).contains(course)){
            throw new RuntimeException("You don't own a course with that name");
        }
        DBObject query = new BasicDBObject("username",listener);
        DBCursor cursor = users.find(query);
        if(!cursor.hasNext()){
            throw new RuntimeException("No such user");
        }
        if(getCourses(listener).contains(course)){
            return;
        }
        DBObject updateQuery = new BasicDBObject("$push",new BasicDBObject("courses", getCourseIdFromName(course)));
        users.update(query,updateQuery);
    }

    private static BasicDBObject getCourse(String login, String course){
        List<BasicDBObject> courses = getCoursesObject(login,editable);
        System.out.println(course);
        for(BasicDBObject course_: courses){
            if(getCourseNameFromId(course_).equals(course)){
                return course_;
            }
        }
        return null;
    }

    private static List<String> getFilesStr(BasicDBObject course){
        String id = course.get("id").toString();
        DBObject query1 = new BasicDBObject("_id",new ObjectId(id));
        DBCursor cursor1 = courses.find(query1);
        return (List<String>) cursor1.one().get("files");

    }

    static String editable = "editableCourses";
    static String allCourses = "courses";

    private static List<BasicDBObject> getCoursesObject(String login, String coursesField){
        DBObject query = new BasicDBObject("username",login);
        DBCursor cursor = users.find(query);
        List<BasicDBObject> userCourses = (List<BasicDBObject>) cursor.one().get(coursesField);
        return userCourses;
    }

    private static String getCourseNameFromId(BasicDBObject course){
        String id = course.get("id").toString();
        DBObject query1 = new BasicDBObject("_id",new ObjectId(id));
        DBCursor cursor1 = courses.find(query1);
        return cursor1.one().get("name").toString();
    }

    private static BasicDBObject getCourseIdFromName(String course){
        DBObject query1 = new BasicDBObject("name", course);
        DBCursor cursor1 = courses.find(query1);
        return new BasicDBObject("id", cursor1.one().get("_id"));
    }

}
