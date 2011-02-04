package org.dant.MyBookmarks;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.AsyncTask;
import android.provider.Browser;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Button;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.graphics.Typeface;
import android.view.Window;

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
    private boolean showButton = false;
    ProgressDialog dialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/Ubuntu-R.ttf");
        
        Button button;

        
        //TODO: A user interface would be nice
        //TODO: Add backup and restore capabilities

        // Programmatically load text from an asset and place it into the
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
            tv.setTypeface(tf);
            tv.setText(text);
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }

        // http://developer.android.com/guide/topics/data/data-storage.html#filesExternal
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            // TODO: custom view to display success.  Needs "check" icon.
            mExternalStorageAvailable = mExternalStorageWriteable = showButton = true;
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
        button.setTypeface(tf);
        button.setOnClickListener(this);
         
//        setIconState();
   
//        if (mExternalStorageAvailable && mExternalStorageWriteable) {
//            new BookmarkExportTask().execute();
//        }            
    } //onCreate
    
//     protected void exportTask ()
//     {
//         String appPath = new String("my_bookmarks");
        
//         try {            
//             File root = Environment.getExternalStorageDirectory();
//             File dir = new File (root.getAbsolutePath() + "/" + appPath);
//             dir.mkdirs();
            
//             File exfile = new File(dir, appPath +".html");
            
//             FileOutputStream exportFile = new FileOutputStream(exfile);
            
//             //query the bookmarks uri, filter by "bookmark" column
//             Cursor mCur = managedQuery(android.provider.Browser.BOOKMARKS_URI,null,
//            		android.provider.Browser.BookmarkColumns.BOOKMARK, null, null
//            		);
           	
           	
//            	int bCount = mCur.getCount();
//            	System.out.println("found " + bCount + " bookmarks");
//            	dialog.setMax(bCount);
           	
// //         	if (DEBUG) Log.v(TAG, "Found " + bCount + " bookmarks");
           	               		
//             mCur.moveToFirst();
            
//             int titleIdx = mCur.getColumnIndex(Browser.BookmarkColumns.TITLE);
//             int bookmarkIdx = mCur.getColumnIndex(Browser.BookmarkColumns.BOOKMARK);
//             int faviconIdx = mCur.getColumnIndex(Browser.BookmarkColumns.FAVICON);
//             int createdIdx = mCur.getColumnIndex(Browser.BookmarkColumns.CREATED);
//             int urlIdx = mCur.getColumnIndex(Browser.BookmarkColumns.URL);
//             int dateIdx = mCur.getColumnIndex(Browser.BookmarkColumns.DATE);
//             int visitsIdx = mCur.getColumnIndex(Browser.BookmarkColumns.VISITS);

//             // set up bookmark export file header
//             String bmHeader = new String ("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n");
//             bmHeader += "<!-- This is an automatically generated file.\n";
//             bmHeader += "     It will be read and overwritten.\n";
//             bmHeader += "     DO NOT EDIT! -->\n";
//             bmHeader += "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n";
//             bmHeader += "<TITLE>Bookmarks</TITLE>\n";
//             bmHeader += "<H1>Bookmarks</H1>";
//             bmHeader += "<DL><p>\n<DT><H3 ADD_DATE=\"0\" LAST_MODIFIED=\"1293023299\" PERSONAL_TOOLBAR_FOLDER=\"true\">Bookmarks Bar</H3>\n<DL><p>\n";
                        
//             exportFile.write(bmHeader.getBytes());

//             while (mCur.isAfterLast() == false) {
//             	String title = new String(mCur.getString(titleIdx));
// //                    	String bkmk = new String("\n" + mCur.getString(bookmarkIdx)); /* 1 if bm, 0 if history */
// //                    	view.append("\n" + mCur.getString(faviconIdx));
// //                    	String created = new String(mCur.getString(createdIdx));
//             	String url = new String(mCur.getString(urlIdx));
//             	String date = new String(mCur.getString(dateIdx));
// //                    	String visits = new String("\n" + mCur.getString(visitsIdx));
            	
//             	//build export line
//             	String bookmarkExport = new String("<DT><A HREF=\"" + url + "\" ADD_DATE=\"" + date + "\">" + title + "</A>\n");
            	
//             	exportFile.write(bookmarkExport.getBytes());
//             	dialog.incrementProgressBy(1);
//             	mCur.moveToNext();
//             }
                                    
//             dialog.setProgress(bCount);
//             // clean up the file
//             String tail = new String("</DL><p>");
//             exportFile.write(tail.getBytes());
//             exportFile.flush();
//             exportFile.close();
//         } catch (Exception e) {
//             System.out.println("Error exporting file " + e.getMessage());
//         }
//     } // exportTask

    private void setIconState()
    {
//        // TODO: set icon states
//        if (mExternalStorageAvailable && mExternalStorageWriteable){
//            //TODO: show sdcard-writable
//            //TODO: show sdcard-available
//        } else if (mExternalStorageAvailable && (!mExternalStorageWriteable)) {
//            //TODO: show sdcard-available
//            //TODO: show sdcard-unwritable
//        } else if (!mExternalStorageAvailable || mExternalStorageWriteable) {
//            //TODO: show sdcard-unavailable
//            //TODO: show sdcard-unwritable
//        }
    }

    @Override
    public void onClick(View v) {
        // Activity.requestWindowFeature(Window.FEATURE_PROGRESS);

//        dialog = ProgressDialog.show(MyBookmarks.this, "", "Exporting...", true);
        // exportTask();
        new StartExportTask().execute(); 
        // dialog.dismiss();
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
                
                //query the bookmarks uri, filter by "bookmark" column
                Cursor mCur = managedQuery(android.provider.Browser.BOOKMARKS_URI,null,
                    android.provider.Browser.BookmarkColumns.BOOKMARK, null, null
                    );
                
                
                int bCount = mCur.getCount();
                System.out.println("found " + bCount + " bookmarks");
                // dialog.setMax(bCount);
                
    //          if (DEBUG) Log.v(TAG, "Found " + bCount + " bookmarks");
                                    
                mCur.moveToFirst();
                
                int titleIdx = mCur.getColumnIndex(Browser.BookmarkColumns.TITLE);
                int bookmarkIdx = mCur.getColumnIndex(Browser.BookmarkColumns.BOOKMARK);
                int faviconIdx = mCur.getColumnIndex(Browser.BookmarkColumns.FAVICON);
                int createdIdx = mCur.getColumnIndex(Browser.BookmarkColumns.CREATED);
                int urlIdx = mCur.getColumnIndex(Browser.BookmarkColumns.URL);
                int dateIdx = mCur.getColumnIndex(Browser.BookmarkColumns.DATE);
                int visitsIdx = mCur.getColumnIndex(Browser.BookmarkColumns.VISITS);

                // set up bookmark export file header
                String bmHeader = new String ("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n");
                bmHeader += "<!-- This is an automatically generated file.\n";
                bmHeader += "     It will be read and overwritten.\n";
                bmHeader += "     DO NOT EDIT! -->\n";
                bmHeader += "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n";
                bmHeader += "<TITLE>Bookmarks</TITLE>\n";
                bmHeader += "<H1>Bookmarks</H1>";
                bmHeader += "<DL><p>\n<DT><H3 ADD_DATE=\"0\" LAST_MODIFIED=\"1293023299\" PERSONAL_TOOLBAR_FOLDER=\"true\">Bookmarks Bar</H3>\n<DL><p>\n";
                            
                exportFile.write(bmHeader.getBytes());

                while (mCur.isAfterLast() == false) {
                    String title = new String(mCur.getString(titleIdx));
    //                      String bkmk = new String("\n" + mCur.getString(bookmarkIdx)); /* 1 if bm, 0 if history */
    //                      view.append("\n" + mCur.getString(faviconIdx));
    //                      String created = new String(mCur.getString(createdIdx));
                    String url = new String(mCur.getString(urlIdx));
                    String date = new String(mCur.getString(dateIdx));
    //                      String visits = new String("\n" + mCur.getString(visitsIdx));
                    
                    //build export line
                    String bookmarkExport = new String("<DT><A HREF=\"" + url + "\" ADD_DATE=\"" + date + "\">" + title + "</A>\n");
                    
                    exportFile.write(bookmarkExport.getBytes());
                    dialog.incrementProgressBy(1);
                    mCur.moveToNext();
                }
                                        
                // dialog.setProgress(bCount);
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
        }
    }// end StartExportTask



} // MyBookmarks