package com.friends.android;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.friends.android.internal.Constans;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.umeng.analytics.MobclickAgent;

public class MainApplication extends Application {

    public static final String DEBUG_TAG = "MainApplication";
    
    public static File filesPath;
    public static File voicesPath;
    public static File picturesPath;
    public static SharedPreferences prefs;
    public static String userId = null;
    public static String userToken = null;
    public static String[] imageHeaders = null;
    public static int width = 0;
    public static ImageLoader imageLoader;
    
    public MainApplication() {
        
    }
    
    @Override
    public void onCreate() {
        
        MobclickAgent.openActivityDurationTrack(false);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        userId = prefs.getString(Constans.USER_ID, null);
        userToken = prefs.getString(Constans.USER_TOKEN, null);
        if (userToken != null) {
            imageHeaders = new String[] {"X-app-token", "", "X-user-token", userToken, "Authorization", "Basic " + userToken};
        }
        WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        
        filesPath = new File(Environment.getExternalStorageDirectory(), "2DFriend");
        filesPath.mkdirs();
        voicesPath = new File(filesPath, "voices");
        voicesPath.mkdirs();
        picturesPath = new File(filesPath, "pictures");
        picturesPath.mkdirs();
        
        initImageLoader(getApplicationContext());
        imageLoader = ImageLoader.getInstance();
        
    }
    
    public void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(),
            MainApplication.filesPath.getAbsolutePath());

        DisplayImageOptions defaultOption = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true)
            ./*considerExifParams(false).*/imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .displayer(new FadeInBitmapDisplayer(300)).resetViewBeforeLoading(true)
            .showImageForEmptyUri(R.drawable.ic_launcher).showImageOnFail(R.drawable.ic_launcher).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).memoryCacheExtraOptions(480, 800)
            .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
            .discCache(new UnlimitedDiscCache(cacheDir))./*discCache(new UnlimitedDiscCache(cacheDir)).*/discCacheFileNameGenerator(new HashCodeFileNameGenerator())
            .defaultDisplayImageOptions(defaultOption).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
      }
}
