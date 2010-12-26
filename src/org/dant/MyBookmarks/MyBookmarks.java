package org.dant.MyBookmarks;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.AsyncTask;
import android.provider.Browser;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.String;
 
/*  Browser.BookmarkColumns.TITLE
    Browser.BookmarkColumns.BOOKMARK
    Browser.BookmarkColumns.FAVICON
    Browser.BookmarkColumns.CREATED
    Browser.BookmarkColumns.URL
    Browser.BookmarkColumns.DATE
    Browser.BookmarkColumns.VISITS 
 */


public class MyBookmarks extends Activity
{
    private ProgressBar mProgress;            
     
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        Eula.show(this);
        new BookmarkExportTask().execute();
    }
    
    class BookmarkExportTask extends AsyncTask<Void,Integer,Void>
    {
        private String appPath = new String("my_bookmarks");
        
        @Override
        protected Void doInBackground(Void... unused) {
            try {
                File root = Environment.getExternalStorageDirectory();
                if (root.canWrite()){
                    File dir = new File (root.getAbsolutePath() + "/" + appPath);
                    dir.mkdirs();
                    
                    File file = new File(dir, appPath +".html");
                    
                    FileOutputStream f = new FileOutputStream(file);
                    
                    Cursor mCur = managedQuery(android.provider.Browser.BOOKMARKS_URI,
                   		null, null, null, null
                   		);
                   	
                   	// setting progress bar max to # of bookmarks x2 (r,w)	
                   	int pbSize = mCur.getCount() * 2;
                   	mProgress.setMax(pbSize);
                   		
                    mCur.moveToFirst();
                    
                    int titleIdx = mCur.getColumnIndex(Browser.BookmarkColumns.TITLE);
//                    int bookmarkIdx = mCur.getColumnIndex(Browser.BookmarkColumns.BOOKMARK);
//                    int faviconIdx = mCur.getColumnIndex(Browser.BookmarkColumns.FAVICON);
//                    int createdIdx = mCur.getColumnIndex(Browser.BookmarkColumns.CREATED);
                    int urlIdx = mCur.getColumnIndex(Browser.BookmarkColumns.URL);
                    int dateIdx = mCur.getColumnIndex(Browser.BookmarkColumns.DATE);
//                    int visitsIdx = mCur.getColumnIndex(Browser.BookmarkColumns.VISITS);

                    // set up bookmark file header
                    String bmHeader = new String ("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n");
                    bmHeader += "<!-- This is an automatically generated file.\n";
                    bmHeader += "     It will be read and overwritten.\n";
                    bmHeader += "     DO NOT EDIT! -->\n";
                    bmHeader += "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n";
                    bmHeader += "<TITLE>Bookmarks</TITLE>\n";
                    bmHeader += "<H1>Bookmarks</H1>";
                    bmHeader += "<DL><p>\n<DT><H3 ADD_DATE=\"0\" LAST_MODIFIED=\"1293023299\" PERSONAL_TOOLBAR_FOLDER=\"true\">Bookmarks Bar</H3>\n<DL><p>\n";
                    f.write(bmHeader.getBytes());

                    while (mCur.isAfterLast() == false) {
                    	String title = new String(mCur.getString(titleIdx));
//                    	String bkmk = new String("\n" + mCur.getString(bookmarkIdx));
//                    	view.append("\n" + mCur.getString(faviconIdx));
//                    	String created = new String(mCur.getString(createdIdx));
                    	String url = new String(mCur.getString(urlIdx));
                    	String date = new String(mCur.getString(dateIdx));
//                    	String visits = new String("\n" + mCur.getString(visitsIdx));
                    	
                    	//build export line
                    	String bookmarkExport = new String("<DT><A HREF=\"" + url + "\" ADD_DATE=\"" + date + "\">" + title + "</A>\n");
                    	
                    	publishProgress(1);
                    	f.write(bookmarkExport.getBytes());
                    	publishProgress(1);
                    	mCur.moveToNext();
                    }      
                    
                    mProgress.setProgress(pbSize);
                    
                    // clean up the file
                    String tail = new String("</DL><p>");
                    f.write(tail.getBytes());
                    f.flush();
                    f.close();
                }
            } catch (Exception e) {
                System.out.println("Could not write file " + e.getMessage());
            }
            
            return(null);            
        } // doInBackground
    
        @Override
        protected void onPreExecute() {
            mProgress = (ProgressBar) findViewById(R.id.progress_bar);
        }


        @Override
        protected void onProgressUpdate(Integer... value)
        {
            mProgress.incrementProgressBy(value[0].intValue());   
        }
        
        @Override
        protected void onPostExecute(Void unused)
        {
            Toast.makeText(MyBookmarks.this, "Export complete.", Toast.LENGTH_SHORT).show();
        }
        
    } // BookmarkExportTask
} // MyBookmarks
