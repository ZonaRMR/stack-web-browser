package eu.depa.browsing.stack;

import android.app.Application;
import android.os.Environment;
import java.io.File;

public class ApplicationExt extends Application{

    protected File extStorageAppBasePath;
        protected File extStorageAppCachePath;

        @Override
        public void onCreate()
        {
            super.onCreate();

            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
            {
                File externalStorageDir = Environment.getExternalStorageDirectory();

                if (externalStorageDir != null)
                {
                    extStorageAppBasePath = new File(
                            externalStorageDir.getAbsolutePath() +
                            File.separator + "Android" +
                            File.separator + "data" +
                            File.separator + getPackageName());
                }

                if (extStorageAppBasePath != null)
                {
                    extStorageAppCachePath = new File(
                            extStorageAppBasePath.getAbsolutePath() +
                            File.separator + "cache");

                    boolean isCachePathAvailable = true;

                    if (!extStorageAppCachePath.exists())
                        isCachePathAvailable = extStorageAppCachePath.mkdirs();

                    if (!isCachePathAvailable)
                        extStorageAppCachePath = null;
                }
            }
        }

        @Override
        public File getCacheDir()
        {
            if (extStorageAppCachePath != null)
                return extStorageAppCachePath;
            else
                return super.getCacheDir();
        }
}
