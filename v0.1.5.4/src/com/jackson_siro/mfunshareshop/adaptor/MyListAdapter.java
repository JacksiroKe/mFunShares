package com.jackson_siro.mfunshareshop.adaptor;

import com.jackson_siro.mfunshareshop.*;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter{
    
    private Activity activity;
    private String[] imageurl;
    private String[] idtxt, titletxt, descritxt;
    static Context mcontext;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public MyListAdapter(Activity thisact, String[] image, String[] lstid, String[] title, String[] descri) {
        activity = thisact;
        imageurl = image;
        idtxt = lstid;
        titletxt = title;
        descritxt = descri;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return imageurl.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public static class ViewHolder{
        public TextView text;
        public TextView texti;
        public TextView textii;
        public TextView textWide;
        public ImageView image; 
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        View vi=convertView;
        ViewHolder holder;
        
        if(convertView==null){
             
            vi = inflater.inflate(R.layout.cat_listview, null);
             
            holder = new ViewHolder();
            holder.text = (TextView) vi.findViewById(R.id.text);
            holder.texti = (TextView) vi.findViewById(R.id.texti);
            holder.textii=(TextView)vi.findViewById(R.id.textii);
            holder.image=(ImageView)vi.findViewById(R.id.image);
             
            vi.setTag( holder );
        }
        else holder=(ViewHolder)vi.getTag();

        holder.text.setText(idtxt[position]);
        holder.texti.setText(titletxt[position]);
        holder.textii.setText(descritxt[position]);
        ImageView image = holder.image;
        
        imageLoader.DisplayImage(imageurl[position], image);
        //image.setImageBitmap( getRoundedShape(decodeFile(activity, imageurl[position]),200));
        
        return vi; 
        
    }

    public static Bitmap decodeFile(Context context, int resId) {
		try {
	    	// decode image size
	    	mcontext=context;
	    	BitmapFactory.Options o = new BitmapFactory.Options();
	    	o.inJustDecodeBounds = true;
	    	BitmapFactory.decodeResource(mcontext.getResources(), resId, o);
	    	// Find the correct scale value. It should be the power of 2.
	    	final int REQUIRED_SIZE = 200;
	    	int width_tmp = o. outWidth, height_tmp = o. outHeight ;
	    	int scale = 1;
	    	while ( true)
	    	{
		    	 if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
		    		 break ;
		    	 width_tmp /= 2;
		    	 height_tmp /= 2;
		    	 scale++;
	    	}
	    	// decode with inSampleSize
	    	BitmapFactory.Options o2 = new BitmapFactory.Options();
	    	o2. inSampleSize = scale;
	    	return BitmapFactory.decodeResource( mcontext.getResources(), resId, o2);
		} 
		catch (Exception e) {}
	    return null;
    }
    
    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage, int width) {
    	 // TODO Auto-generated method stub
    	 int targetWidth = width;
    	 int targetHeight = width;
    	 Bitmap targetBitmap = Bitmap. createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
    	 Canvas canvas = new Canvas(targetBitmap);
    	 Path path = new Path();
    	 path.addCircle((( float ) targetWidth - 1) / 2,
    	 ((float ) targetHeight - 1) / 2, (Math. min((( float ) targetWidth), 
    			 ((float ) targetHeight)) / 2), Path.Direction. CCW);
    	 canvas.clipPath(path);
    	 Bitmap sourceBitmap = scaleBitmapImage;
    	 canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()), 
    			 new Rect(0, 0, targetWidth, targetHeight), null);
    	 return targetBitmap;
    	 }

}