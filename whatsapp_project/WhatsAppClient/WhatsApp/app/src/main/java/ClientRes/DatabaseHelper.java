package ClientRes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.zemoso.whatsapp.MostRecentUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zemoso on 4/8/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION=1;
    private static String dbOwner=null;
    private static final String DB_NAME="ZEMOSO_WHATSAPP";
    private static final String TABLE_NAME="users";
    private static final String USER_COL="username";
    private static final String SECOND_TABLE="messages";
    SimpleDateFormat dateFormat=null;
    private static DatabaseHelper databaseHelper=null;
    static SharedPreferences sharedPreferences=null;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME+"_"+dbOwner, null, DB_VERSION);
        dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
    }

    public  static DatabaseHelper getInstance(Context context){
        if(databaseHelper==null){
            sharedPreferences=context.getSharedPreferences("zemoso_whatsapp",Context.MODE_PRIVATE);
            dbOwner=sharedPreferences.getString("username","");
            databaseHelper=new DatabaseHelper(context);
        }
        return databaseHelper;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable="create table "+TABLE_NAME+" ("+USER_COL+" varchar(255) primary key, name varchar(255), is_group INTEGER);";
        String secondaryTable="create table "+SECOND_TABLE+"("
                +" message_id INTEGER PRIMARY KEY, source varchar(255), target varchar(255), message TEXT, is_read INTEGER DEFAULT 0, Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, "+
                "FOREIGN KEY(source) REFERENCES "+TABLE_NAME+"(username), "+
                " FOREIGN KEY(target) REFERENCES "+TABLE_NAME+"(username) "+
                ")";
        sqLiteDatabase.execSQL(createTable);
        sqLiteDatabase.execSQL(secondaryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+SECOND_TABLE);
        sqLiteDatabase.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addUser(String username,String name,Integer is_group){
        SQLiteDatabase sqLiteDatabase=databaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(USER_COL,username);
        contentValues.put("name",name);
        contentValues.put("is_group",is_group);
        try{
            sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        } catch (SQLiteConstraintException e){
            Log.e("User already present","");
        } catch (Exception e){
            Log.e("Insert exception",e.toString());
        }finally {
        }

    }
    public void removeUser(String username){
        SQLiteDatabase sqLiteDatabase=databaseHelper.getReadableDatabase();
        String args[]={username};
        sqLiteDatabase.delete(TABLE_NAME,USER_COL+" = ?",args);

    }
    public List<Users> getAllUsers(){
        List<Users> usersList=new ArrayList<Users>();
        String getUsersQuery="select * from "+TABLE_NAME;
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(getUsersQuery,null);
        if(cursor.moveToFirst()){
            do{
                Users users=new Users(cursor.getString(0),cursor.getString(1),cursor.getInt(2));
                usersList.add(users);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return usersList;
    }
    public boolean containsUser(String username){
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        int count = 0;
        Cursor cursor=null;
        try {
            String queryString = "select * from " + TABLE_NAME + " where username=?";
            cursor = sqLiteDatabase.rawQuery(queryString, new String[]{username});
            count=cursor.getCount();
        }
        catch (Exception e){
            Log.e("containUser",e.toString());
        }finally {
            cursor.close();

        }
        if(count==0)
            return false;
        return true;
    }
    public void addMessage(Integer id,String source,String target,String message,String timestamp){
        SQLiteDatabase sqLiteDatabase=databaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("message_id",id);
        contentValues.put("source",source);
        contentValues.put("target",target);
        contentValues.put("message",message);
        contentValues.put("timestamp",timestamp);
        try{
            sqLiteDatabase.insert(SECOND_TABLE,null,contentValues);
        }
        catch (SQLiteConstraintException e){
            Log.w("Duplicate entry","");
        }
        catch (Exception e){
            Log.e("addMessage",e.toString());
        }

        finally {

        }

    }

    public void addMessage(Integer id,String source,String target,String message){
        SQLiteDatabase sqLiteDatabase=databaseHelper.getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("message_id",id);
        contentValues.put("source",source);
        contentValues.put("target",target);
        contentValues.put("message",message);

        Date d=new Date();
        contentValues.put("timestamp",dateFormat.format(d));
        try{
            sqLiteDatabase.insert(SECOND_TABLE,null,contentValues);
        }
        catch (SQLiteConstraintException e){
            Log.w("Duplicate entry","");

        }
        catch (Exception e){
            Log.e("addMessage",e.toString());
        }
        finally {

        }

    }
    public List<UserMessages> getMessage(String source,String target){
        List<UserMessages> userMessages=new ArrayList<UserMessages>();
        String queryString="select * from "+SECOND_TABLE+" where source=? and target=? or source=? and target=? order by TIMESTAMP";
        SQLiteDatabase sqLiteDatabase=databaseHelper.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(queryString,new String[]{source,target,target,source});
        if(cursor.moveToFirst()){
            do{
                //message_id INTEGER  primary key, source varchar(255), target varchar(255), message TEXT, is_read INTEGER DEFAULT 0, Timestamp
                UserMessages userMessages1=new UserMessages(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(5),cursor.getInt(4));
                userMessages.add(userMessages1);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return userMessages;
    }

    public List<Users> getAllUsersExcept(String username){
        List<Users> usersList=new ArrayList<Users>();
        String getUsersQuery="select * from "+TABLE_NAME+" where username!=?  order by username";
        Cursor cursor=null;
        SQLiteDatabase sqLiteDatabase=databaseHelper.getReadableDatabase();
        try {
            cursor=sqLiteDatabase.rawQuery(getUsersQuery,new String[]{username});
            if(cursor.moveToFirst()){
                do{
                    Users users=new Users(cursor.getString(0),cursor.getString(1),cursor.getInt(2));
                    usersList.add(users);
                }while (cursor.moveToNext());
            }
        }
        catch (Exception e){
            Log.e("getAllUserExcep",e.toString());
        }finally {
            cursor.close();

        }

        return usersList;
    }
    public String getNameByUsername(String username){
        String name=null;
        SQLiteDatabase sqLiteDatabase=databaseHelper.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("select name from "+TABLE_NAME+" where username=?",new String[]{username});
        cursor.moveToFirst();
        name=cursor.getString(0);
        cursor.close();
        return name;
    }
    public Integer isGroup(String username){
        SQLiteDatabase sqLiteDatabase=databaseHelper.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("select is_group from "+TABLE_NAME+" where username=?",new String[]{username});
        cursor.moveToFirst();
        int x=cursor.getInt(0);
        cursor.close();

        return x;

    }
    public List<MostRecentUserWrapper> getMostRecent(){
        List<MostRecentUserWrapper> usersList=new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=databaseHelper.getReadableDatabase();
        String getMostRecentUsersQuery="select source,target,max(message_id) as temp_d from "+SECOND_TABLE+" group by source,target order by temp_d desc";
        Cursor cursor=sqLiteDatabase.rawQuery(getMostRecentUsersQuery,null);
        if(cursor.moveToFirst()){
            do{
                String source=cursor.getString(0);
                String target=cursor.getString(1);
                int id=cursor.getInt(2);
                if(isValid(usersList,source,target)){

                    if(dbOwner.equals(source))
                        usersList.add(new MostRecentUserWrapper(target,id,getMessageById(id),getTimestampById(id)));
                    else
                        usersList.add(new MostRecentUserWrapper(source,id,getMessageById(id),getTimestampById(id)));
                }
            }while (cursor.moveToNext());
        }cursor.close();

        return usersList;
    }

    public String getMessageById(int id){
        String message=null;
        SQLiteDatabase sqLiteDatabase=databaseHelper.getReadableDatabase();
        String getMessage="select message from "+SECOND_TABLE+" where message_id="+id;
        Cursor cursor=null;
        try{
            cursor=sqLiteDatabase.rawQuery(getMessage,null);
            cursor.moveToFirst();
            message=cursor.getString(0);

        }
        catch (Exception e){
            Log.e("DBHELPER",e.toString());
        }
        finally {
            cursor.close();

        }

        return message;
    }
    public Date getTimestampById(int id){
        Date message=null;
        SQLiteDatabase sqLiteDatabase=databaseHelper.getReadableDatabase();
        String getMessage="select timestamp from "+SECOND_TABLE+" where message_id="+id;
        Cursor cursor=null;
        try{
            cursor=sqLiteDatabase.rawQuery(getMessage,null);
            cursor.moveToFirst();
            message=dateFormat.parse(cursor.getString(0));


        }
        catch (Exception e){
            Log.e("DBH",e.toString());
        }finally {
            cursor.close();

        }

        return message;
    }
    private boolean isValid(List<MostRecentUserWrapper> usersList, String source, String target) {
        for(MostRecentUserWrapper users:usersList){
            if(users.getUsername().equals(source)|users.getUsername().equals(target))
                return false;
        }
    return true;
    }

    public void updateMessageasRead(int o,int requested) {
    SQLiteDatabase sqLiteDatabase=databaseHelper.getWritableDatabase();
        try {
            String sqlUpdateMesssageRead = "update " + SECOND_TABLE + " set is_read="+requested+" where message_id=" + o;
            sqLiteDatabase.execSQL(sqlUpdateMesssageRead);
        }
        catch (Exception e){
            Log.e("updateMessage",e.toString());
        }finally {

        }
    }
    public List<Integer> getIdOfMessages(String source,String target){
        List<Integer> userMessages=new ArrayList<Integer>();
        String queryString="select message_id  from "+SECOND_TABLE+" where source=? and target=? or source=? and target=? order by TIMESTAMP";
        SQLiteDatabase sqLiteDatabase=databaseHelper.getReadableDatabase();
        Cursor cursor=null;
        try {
            cursor = sqLiteDatabase.rawQuery(queryString, new String[]{source, target, target, source});
            if (cursor.moveToFirst()) {
                do {
                    //message_id INTEGER  primary key, source varchar(255), target varchar(255), message TEXT, is_read INTEGER DEFAULT 0, Timestamp
                    userMessages.add(cursor.getInt(0));
                } while (cursor.moveToNext());
            }

        }
        catch (Exception e){
            Log.e("getIdOfMessages",e.toString());
        }finally {
            cursor.close();

        }
        return userMessages;
    }
    public UserMessages getWholeMessageById(int id){
        SQLiteDatabase sqLiteDatabase=databaseHelper.getReadableDatabase();
        String s="select * from "+SECOND_TABLE+" where message_id="+id;
        UserMessages userMessages=null;
        Cursor cursor=null;
        try {
            cursor = sqLiteDatabase.rawQuery(s, null);
            cursor.moveToFirst();
             userMessages= new UserMessages(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(5), cursor.getInt(4));
        }
        catch (Exception e){
            Log.e("getWholeMessage",e.toString());
        }
        finally {
            cursor.close();

        }
        return userMessages;
    }

}
