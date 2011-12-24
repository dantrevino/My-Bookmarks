package org.dant.MyBookmarks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.AsyncTask;
import android.provider.Browser;
import android.database.Cursor;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.String;
import java.io.IOException;
import java.io.InputStream;
 
/*  Browser.BookmarkColumns.TITLE
    Browser.BookmarkColumns.BOOKMARK
    Browser.BookmarkColumns.FAVICON
    Browser.BookmarkColumns.CREATED
    Browser.BookmarkColumns.URL
    Browser.BookmarkColumns.DATE
    Browser.BookmarkColumns.VISITS 
*/

public class MyBookmarks extends Activity implements OnClickListener
{
    static final boolean DEBUG = false;

    private TextView mUserMessage;            
    static final int DIALOG_EXPORT = 0;
    
    private static final String USER_MESSAGE = "user.message";
    ProgressDialog dialog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button button;

        //TODO: Add backup and restore capabilities

        // Programatically load text from an asset and place it into the
        // text view.  Note that the text we are loading is ASCII, so we
        // need to convert it to UTF-16.
        try {
            InputStream is = getAssets().open(USER_MESSAGE);

            // We guarantee that the available method returns the total
            // size of the asset...  of course, this does mean that a single
            // asset can't be more than 2 gigs.
            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string.
            String text = new String(buffer);

            // Finally stick the string into the text view.
            TextView tv = (TextView)findViewById(R.id.txtUserMessage);
            //tv.setTypeface(tf);
            tv.setText(text);
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }

        // TODO: finish better sd card availability checking
        // http://developer.android.com/guide/topics/data/data-storage.html#filesExternal
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            // TODO: custom view to display success.  Needs "check" icon.
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            // TODO: custom view to display error. Use coded "lock" icon.
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
            mUserMessage.append("External storage is read-only. Unable to complete operation.");           
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            // TODO: custom view to display error. Use coded "lock" icon.
            mExternalStorageAvailable = mExternalStorageWriteable = false;
            mUserMessage.append("Error checking external storage. Unable to complete operation.");           
        }

        
        // Setup the button that starts the progress dialog
        button = (Button) findViewById(R.id.start);
        //button.setTypeface(tf);
        button.setOnClickListener(this);
         
    } //onCreate
    
//    }

    public void onClick(View v) {
        new StartExportTask().execute(); 

    }

    private class StartExportTask extends AsyncTask<String, Void, Void> {
        ProgressDialog dialog;
        protected Void doInBackground(String... params) {
            String appPath = new String("my_bookmarks");
            
            try {            
                File root = Environment.getExternalStorageDirectory();
                File dir = new File (root.getAbsolutePath() + "/" + appPath);
                dir.mkdirs();
                
                File exfile = new File(dir, appPath +".html");
                
                FileOutputStream exportFile = new FileOutputStream(exfile);
                
                // query the bookmarks uri, filter by "bookmark" column
                // so that we dont get "history" entries
                Cursor mCur = managedQuery(android.provider.Browser.BOOKMARKS_URI,null,
                    android.provider.Browser.BookmarkColumns.BOOKMARK, null, null
                    );
                
                
                int bCount = mCur.getCount();
                System.out.println("found " + bCount + " bookmarks");
                
    //          if (DEBUG) Log.v(TAG, "Found " + bCount + " bookmarks");
                                    
                mCur.moveToFirst();
                
                int titleIdx = mCur.getColumnIndex(Browser.BookmarkColumns.TITLE);
                int bookmarkIdx = mCur.getColumnIndex(Browser.BookmarkColumns.BOOKMARK);
                int faviconIdx = mCur.getColumnIndex(Browser.BookmarkColumns.FAVICON);
                int createdIdx = mCur.getColumnIndex(Browser.BookmarkColumns.CREATED);
                int urlIdx = mCur.getColumnIndex(Browser.BookmarkColumns.URL);
                int dateIdx = mCur.getColumnIndex(Browser.BookmarkColumns.DATE);
                int visitsIdx = mCur.getColumnIndex(Browser.BookmarkColumns.VISITS);
                int unixtime = (int) (System.currentTimeMillis() / 1000L);

                // set up bookmark export file header
                String bmHeader = new String ("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n");
                bmHeader += "<!-- This is an automatically generated file.\n";
                bmHeader += "     It will be read and overwritten.\n";
                bmHeader += "     DO NOT EDIT! -->\n";
                bmHeader += "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n";
                bmHeader += "<TITLE>Bookmarks</TITLE>\n";
                bmHeader += "<H1>Bookmarks</H1>";
                bmHeader += "<DL><p>\n<DT><H3 ADD_DATE=\"0\" LAST_MODIFIED=\"" + unixtime + "\" PERSONAL_TOOLBAR_FOLDER=\"true\">Bookmarks Bar</H3>\n<DL><p>\n";
                            
                exportFile.write(bmHeader.getBytes());

                // TODO: quit creating so many Strings!
                while (mCur.isAfterLast() == false) {
                    String title = new String(mCur.getString(titleIdx));
                    String url = new String(mCur.getString(urlIdx));
                    String date = new String(mCur.getString(dateIdx));
                    
                    //build export line
                    String bookmarkExport = new String("<DT><A HREF=\"" + url + "\" ADD_DATE=\"" + date + "\">" + title + "</A>\n");
                    
                    exportFile.write(bookmarkExport.getBytes());
                    dialog.incrementProgressBy(1);
                    mCur.moveToNext();
                }
                                        
                // clean up the file
                String tail = new String("</DL><p>");
                exportFile.write(tail.getBytes());
                exportFile.flush();
                exportFile.close();
            } catch (Exception e) {
                System.out.println("Error exporting file " + e.getMessage());
            }

            return null;
        }

        protected void onPreExecute() {
            dialog = ProgressDialog.show(MyBookmarks.this, "Export", "Export in progress.", true);
        }

        protected void onPostExecute(Void Result)
        {
            dialog.dismiss();
            Context context = getApplicationContext();
            CharSequence text = "Export Complete!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }// end StartExportTask
} // MyBookmarks
