package com.davidgerstenmier.peoplefinder.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

public class MediaIntentManager {

    public static void launchCamera(Activity activity){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


    }
}
