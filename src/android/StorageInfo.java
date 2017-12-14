package com.mrustudio.plugin.storageinfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.*;
import org.json.JSONObject;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.lang.*;
import android.os.StatFs;
import android.os.Environment;


public class StorageInfo extends CordovaPlugin {
    public static final String EXTERNAL = "external";
    public static final String INTERNAL = "internal";
    public static final String FIXED_PATH = "Android/data/";
    public static final String SD_CARD = "Storage";

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getPath")) {
            Context context = this.cordova.getActivity().getApplicationContext();
            String[] storageDirectories = getStorageDirectories(context);
            String strPackageName = this.cordova.getActivity().getPackageName();
            String strDynamicPath = FIXED_PATH + strPackageName;

            JSONObject jsonObject = new JSONObject();
            JSONObject jsonSubobj = new JSONObject();
            for (int j = 0; j < storageDirectories.length; j++) {
                if (storageDirectories[j].contains("emulated")) {
                    jsonSubobj.put(INTERNAL, "file://" + storageDirectories[j] + File.separator + strDynamicPath + "/");
                
                } else {
                    jsonSubobj.put(EXTERNAL, "file://" + storageDirectories[j] + File.separator + strDynamicPath + "/");
                }
                // push object
                jsonObject.putOpt(SD_CARD, jsonSubobj);
            }
            // return callbackContext success.
            callbackContext.success(jsonObject.toString());
            return true;
        
        } else if (action.equals("getSpace")) { 
            Context context = this.cordova.getActivity().getApplicationContext();
            String[] storageDirectories = getStorageDirectories(context);
            JSONArray details = new JSONArray();
            
            for (int i = 0; i < storageDirectories.length; i++) {
                String directory = storageDirectories[i];
                File f = new File(directory);
                JSONObject detail = new JSONObject();
                if (f.canRead()) {
                    if (storageDirectories[i].contains("emulated")) {
                        // detail.put("internalPath", "file://" + directory + File.separator + "/");
                        detail.put("internalUsedSpace", getUsedSpaceInBytes(INTERNAL));
                        detail.put("internalFreeSpace", getFreeSpaceInBytes(INTERNAL));
                        detail.put("internalTotalSpace", getTotalSpaceInBytes(INTERNAL));
                
                    } else {
                        // detail.put("externalPath", "file://" + directory + File.separator + "/");
                        detail.put("externalUsedSpace", getUsedSpaceInBytes(EXTERNAL));
                        detail.put("externalFreeSpace", getFreeSpaceInBytes(EXTERNAL));
                        detail.put("externalTotalSpace", getTotalSpaceInBytes(EXTERNAL));
                    }
                    
            
                } else if (!f.canRead()) {
                    if (storageDirectories[i].contains("emulated")) {
                        // detail.put("internalPath", "file://" + directory + File.separator + "/");
                        detail.put("internalUsedSpace", getUsedSpaceInBytes(INTERNAL));
                        detail.put("internalFreeSpace", getFreeSpaceInBytes(INTERNAL));
                        detail.put("internalTotalSpace", getTotalSpaceInBytes(INTERNAL));

                    } else {
                        // detail.put("externalPath", "file://" + directory + File.separator + "/");
                        detail.put("externalUsedSpace", getUsedSpaceInBytes(EXTERNAL));
                        detail.put("externalFreeSpace", getFreeSpaceInBytes(EXTERNAL));
                        detail.put("externalTotalSpace", getTotalSpaceInBytes(EXTERNAL));
                    }
                
                } else {
                    detail.put("errorMessage", "access failed Storage directory.");
                }

                details.put(detail);
            }
            callbackContext.success(details);
            return true;

        } else 
        return false;
    }


    /**
     * Given a path return the number of used bytes in the filesystem containing the path.
     *
     * @param path to the file system
     * @return used space in bytes
    */
    private long getUsedSpaceInBytes(String path) {
        File paths;
        if (path == EXTERNAL) {
            paths = Environment.getExternalStorageDirectory();

        } else {
            paths = Environment.getDataDirectory();
        }

        try {
            StatFs stat = new StatFs(paths.getPath());
            long blockSize = stat.getBlockCountLong();
            return blockSize;
            
        } catch (IllegalArgumentException e) {
            // The path was invalid. Just return 0 free bytes.
            return 0;
        }
    }


    /**
     * Given a path return the number of free bytes in the filesystem containing the path.
     *
     * @param path to the file system
     * @return free space in bytes
     */

    private long getFreeSpaceInBytes(String path) {
        File paths;
        if (path == EXTERNAL) {
            paths = Environment.getExternalStorageDirectory();

        } else {
            paths = Environment.getDataDirectory();
        }

        try {
            StatFs stat = new StatFs(paths.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return availableBlocks * blockSize;

        } catch (IllegalArgumentException e) {
            // The path was invalid. Just return 0 free bytes.
            return 0;
        }
    }


    /**
     * Given a path return the number of total bytes in the filesystem containing the path.
     *
     * @param path to the file system
     * @return free space in bytes
     */
    private long getTotalSpaceInBytes(String path) {
        File paths;
        if (path == EXTERNAL) {
            paths = Environment.getExternalStorageDirectory();

        } else {
            paths = Environment.getDataDirectory();
        }

        try {
            StatFs stat = new StatFs(paths.getPath());
            long totalSize = stat.getTotalBytes();
            return totalSize;
        } catch (IllegalArgumentException e) {
            // The path was invalid. Just return 0 free bytes.
            return 0;
        }
    }

  /**
   * Returns all available SD-Cards in the system (include emulated)
   * <p>
   * Warning: Hack! Based on Android source code of version 4.3 (API 18)
   * Because there is no standard way to get it.
   *
   * @return paths to all available SD-Cards in the system (include emulated)
   */
  public static String[] getStorageDirectories(Context pContext) {
    // Final set of paths
    final Set<String> rv = new HashSet<String>();

    //Get primary & secondary external device storage (internal storage & micro SDCARD slot...)
    File[] listExternalDirs = ContextCompat.getExternalFilesDirs(pContext, null);
    for (int i = 0; i < listExternalDirs.length; i++) {
      if (listExternalDirs[i] != null) {
        String path = listExternalDirs[i].getAbsolutePath();
        int indexMountRoot = path.indexOf("/Android/data/");
        if (indexMountRoot >= 0 && indexMountRoot <= path.length()) {
          //Get the root path for the external directory
          rv.add(path.substring(0, indexMountRoot));
        }
      }
    }
    return rv.toArray(new String[rv.size()]);
  }
}
