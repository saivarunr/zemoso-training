package ClientRes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
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
    private String dbOwner=null;
    private static final String DB_NAME="ZEMOSO_WHATSAPP";
    private static final String TABLE_NAME="users";
    private static final String USER_COL="username";
    private static final String SECOND_TABLE="messages";
    SimpleDateFormat dateFormat=null;
    public DatabaseHelper(Context context,String username) {
        super(context, DB_NAME+"_"+username, null, DB_VERSION);
        dbOwner=username;
        dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable="create table "+TABLE_NAME+" ("+USER_COL+" varchar(255) primary key);";
        String secondaryTable="create table "+SECOND_TABLE+"("
                +" message_id INTEGER  primary key, source varchar(255), target varchar(255), message TEXT, Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, "+
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

    public void addUser(String username){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(USER_COL,username);
        try{
            sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        }
        catch (Exception e){
            Log.e("Insert exception",e.toString());
        }
        sqLiteDatabase.close();
    }
    public void removeUser(String username){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String args[]={username};
        sqLiteDatabase.delete(TABLE_NAME,USER_COL+" = ?",args);
        sqLiteDatabase.close();
    }
    public List<Users> getAllUsers(){
        List<Users> usersList=new ArrayList<Users>();
        String getUsersQuery="select * from "+TABLE_NAME;
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(getUsersQuery,null);
        if(cursor.moveToFirst()){
            do{
                Users users=new Users(cursor.getString(0));
                usersList.add(users);
            }while (cursor.moveToNext());
        }
        return usersList;
    }
    public boolean containsUser(String username){
        String queryString="select * from "+TABLE_NAME+" where username=?";
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(queryString,new String[]{username});
        int count=cursor.getCount();
        if(count==0)
            return false;
        return true;
    }
    public void addMessage(String source,String target,String message,String timestamp){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("source",source);
        contentValues.put("target",target);
        contentValues.put("message",message);
        contentValues.put("timestamp",timestamp);
        try{
            sqLiteDatabase.insert(SECOND_TABLE,null,contentValues);
        }
        catch (Exception e){

        }
        sqLiteDatabase.close();
    }

    public void addMessage(String source,String target,String message){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("source",source);
        contentValues.put("target",target);
        contentValues.put("message",message);

        Date d=new Date();
        contentValues.put("timestamp",dateFormat.format(d));
        try{
            sqLiteDatabase.insert(SECOND_TABLE,null,contentValues);
        }
        catch (Exception e){

        }
        sqLiteDatabase.close();
    }
    public List<UserMessages> getMessage(String source,String target){
        List<UserMessages> userMessages=new ArrayList<UserMessages>();
        String queryString="select * from "+SECOND_TABLE+" where source=? and target=? or source=? and target=? order by TIMESTAMP";
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(queryString,new String[]{source,target,target,source});
        if(cursor.moveToFirst()){
            do{
                UserMessages userMessages1=new UserMessages(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
                userMessages.add(userMessages1);
            }while (cursor.moveToNext());
        }
        return userMessages;
    }

    public List<Users> getAllUsersExcept(String username){
        List<Users> usersList=new ArrayList<Users>();
        String getUsersQuery="select * from "+TABLE_NAME+" where username!=?";
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(getUsersQuery,new String[]{username});
        if(cursor.moveToFirst()){
            do{
                Users users=new Users(cursor.getString(0));
                usersList.add(users);
            }while (cursor.moveToNext());
        }
        return usersList;
    }

    public List<MostRecentUserWrapper> getMostRecent(){
        List<MostRecentUserWrapper> usersList=new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
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
        }
        return usersList;
    }

    public String getMessageById(int id){
        String message=null;
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String getMessage="select message from "+SECOND_TABLE+" where message_id="+id;

        try{
            Cursor cursor=sqLiteDatabase.rawQuery(getMessage,null);
            cursor.moveToFirst();
            message=cursor.getString(0);

        }
        catch (Exception e){
            Log.e("DBHELPER",e.toString());
        }

        return message;
    }
    public Date getTimestampById(int id){
        Date message=null;
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String getMessage="select timestamp from "+SECOND_TABLE+" where message_id="+id;

        try{
            Cursor cursor=sqLiteDatabase.rawQuery(getMessage,null);
            cursor.moveToFirst();
            message=dateFormat.parse(cursor.getString(0));


        }
        catch (Exception e){
            Log.e("DBH",e.toString());
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

}
