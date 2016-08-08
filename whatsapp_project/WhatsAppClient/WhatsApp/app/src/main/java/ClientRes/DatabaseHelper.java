package ClientRes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zemoso on 4/8/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION=1;
    private static final String DB_NAME="ZEMOSO_WHATSAPP";
    private static final String TABLE_NAME="users";
    private static final String USER_COL="username";
    private static final String SECOND_TABLE="messages";
    public DatabaseHelper(Context context,String username) {
        super(context, DB_NAME+"_"+username, null, DB_VERSION);
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

    public void addMessage(String source,String target,String message){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("source",source);
        contentValues.put("target",target);
        contentValues.put("message",message);
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

    public List<Users> getMostRecent(){
        List<Users> usersList=new ArrayList<>();
        String getMostRecentUsers="select  target from "+SECOND_TABLE+" group by message_id ";
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(getMostRecentUsers,null);
        if(cursor.moveToFirst()){
            do{
                Users users=new Users(cursor.getString(0));
                usersList.add(users);
            }while(cursor.moveToFirst());
        }
        return usersList;
    }
}
