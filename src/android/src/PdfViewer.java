package com.qapint.cordova.pdfviewer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.artifex.mupdfviewer.MuPDFActivity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;

public class PdfViewer extends CordovaPlugin {
    protected static final String LOG_TAG = "PdfViewer";
    private CallbackContext callbackContext;

    public boolean onOverrideUrlLoading(String url) {
        if (url.startsWith("file") && url.endsWith(".pdf")) {
            openPdf(url);
            return true;
        } else {
            return super.onOverrideUrlLoading(url);
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.d(LOG_TAG, action);
        this.callbackContext = callbackContext;

        if (!action.equals("openDocument")) {
            return returnWithError("Wrong action name. Only openDocument action can be defined");
        }
        if(args.length() == 0){
            return returnWithError("Document path empty or not defined");
        }

        String path = args.getString(0);
        if (path.isEmpty()) {
            return returnWithError("Document path empty or not defined");
        }else{
            openPdf(path);
            return true;
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
        in.close();
        out.flush();
        out.close();
    }

    private Uri processFileLocation(String filePath){
        if(!filePath.startsWith("file:///android_asset")){
            return Uri.parse(filePath);
        }else{
            Context ctx = webView.getContext();
            int lastSlashPos = filePath.lastIndexOf('/');
            String assetPath = filePath.replace("file:///android_asset/", "");
            String cachePath = ctx.getCacheDir().getAbsolutePath();
            String filename = lastSlashPos == -1 ? filePath : filePath.substring(lastSlashPos+1);
            final File directory = new File(cachePath + "/" + assetPath);

            if (!directory.exists()) {
                directory.mkdirs();
            }
            File outFile = new File(directory, filename);

            try{
                InputStream ins = ctx.getAssets().open(assetPath);
                OutputStream outs = new FileOutputStream(outFile);
                copyFile(ins, outs);
            }catch(IOException e){
                LOG.e(LOG_TAG, e.getMessage());
            }

            return Uri.parse(outFile.getAbsolutePath());
        }
    }

    private void openPdf(String path) {
        Uri fileUri = processFileLocation(path);
        Intent intent = new Intent(webView.getContext(), MuPDFActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(fileUri);
        webView.getContext().startActivity(intent);
    }

    private boolean returnWithError(String message){
        PluginResult result = new PluginResult(PluginResult.Status.ERROR, message);
        Toast.makeText(webView.getContext(), message, Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, message);
        callbackContext.sendPluginResult(result);
        return false;
    }
}
