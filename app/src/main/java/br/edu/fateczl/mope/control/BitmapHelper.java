package br.edu.fateczl.mope.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import br.edu.fateczl.mope.R;

public class BitmapHelper {
    public static BitmapDescriptor drawableToBitmap(Context context, Drawable drawable, int colorId) {
        if(drawable==null)
            return null;

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0,0, canvas.getWidth(),canvas.getHeight());
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, colorId));

        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static BitmapDescriptor getOnibusBitmapDesc(Context context){
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.baseline_directions_bus_24);
        return drawableToBitmap(context, drawable, R.color.bus_icon);
    }

    public static BitmapDescriptor getLocalAtualBitmapDesc(Context context){
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.baseline_my_location_24);
        return drawableToBitmap(context, drawable, R.color.user_location_icon);
    }

}
