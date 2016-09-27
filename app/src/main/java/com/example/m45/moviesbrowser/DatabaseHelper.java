package com.example.m45.moviesbrowser;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by m45 on 8/7/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE `movies` (\n" +
                "\t`_id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                "\t`moviedb_id`\tINTEGER NOT NULL UNIQUE,\n" +
                "\t`title`\tTEXT NOT NULL,\n" +
                "\t`year`\tINTEGER,\n" +
                "\t`runtime`\tTEXT,\n" +
                "\t`rating`\tTEXT,\n" +
                "\t`plot`\tTEXT,\n" +
                "\t`poster`\tTEXT,\n" +
                "\t`back_drop`\tTEXT\n" +
                ");");
        sqLiteDatabase.execSQL("CREATE TABLE `trailers` (\n" +
                "\t`_id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                "\t`moviedb_id`\tINTEGER NOT NULL,\n" +
                "\t`name`\tTEXT NOT NULL,\n" +
                "\t`key`\tTEXT NOT NULL\n" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }


    public Cursor getMovies() {
        return getReadableDatabase().rawQuery("SELECT * FROM movies", null);
    }

    public Cursor getMovie(int movieId) {
        return getReadableDatabase().rawQuery("SELECT * FROM movies WHERE moviedb_id = '" + movieId + "'", null);
    }

    public Cursor getTrailers(int movieId) {
        return getReadableDatabase().rawQuery("SELECT * FROM trailers WHERE moviedb_id = '" + movieId + "'", null);
    }

    public void saveMovie(int movieId, String title, String year, String runtime, String rating,
                          String plot, String poster, String back_drop) {

        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement("INSERT INTO movies (moviedb_id,title,year,runtime,rating,plot,poster,back_drop) VALUES (?, ?,?,?,?,?,?,?);");
        stmt.bindString(1, movieId+"");
        stmt.bindString(2, title);
        stmt.bindString(3, year);
        stmt.bindString(4, runtime);
        stmt.bindString(5, rating);
        stmt.bindString(6, plot);
        stmt.bindString(7, poster);
        stmt.bindString(8, back_drop);

        stmt.execute();
    }

    public void deleteMovie(int movieId) {
        getWritableDatabase().execSQL("DELETE FROM movies WHERE moviedb_id = '" + movieId + "'");
    }

    public void saveTrailer(int movieId, String name, String key) {
        getWritableDatabase().execSQL("INSERT INTO `trailers` (moviedb_id,name,key) " +
                "VALUES (?,?,?);", new String[] { ""+movieId, name, key });
    }

    public boolean isFavoriteMovie(int movieId) {

        return getMovie(movieId).moveToNext();
    }


}
