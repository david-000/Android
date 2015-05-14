package com.example.myportfolio;

import java.io.File;
import java.util.ArrayList;

import com.example.myportfolio.gallary.LRUCache;
import com.example.myportfolio.gallary.ThumbImageInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class UploadActivity extends Activity implements ListView.OnScrollListener, GridView.OnItemClickListener{

	 boolean mBusy = false;
	  ProgressDialog mLoagindDialog;
	  GridView mGvImageList;
	  ImageAdapter mListAdapter;
	  ArrayList<ThumbImageInfo> mThumbImageInfoList;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_list_view);
		
	    mThumbImageInfoList = new ArrayList<ThumbImageInfo>();
	    mGvImageList = (GridView) findViewById(R.id.gvImageList);
	    mGvImageList.setOnScrollListener(this);
	    mGvImageList.setOnItemClickListener(this);
	    
	    new DoFindImageList().execute();
	}
	
	 private long findThumbList()
	  {
	    long returnValue = 0;


	    String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };


	    Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DATE_ADDED + " desc ");

	    if (imageCursor != null && imageCursor.getCount() > 0)
	    {

	      int imageIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media._ID); 
	      int imageDataCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);


	      while (imageCursor.moveToNext())
	      {
	        ThumbImageInfo thumbInfo = new ThumbImageInfo();

	        thumbInfo.setId(imageCursor.getString(imageIDCol));
	        thumbInfo.setData(imageCursor.getString(imageDataCol));
	        thumbInfo.setCheckedState(false);
	        
	        mThumbImageInfoList.add(thumbInfo);
	        returnValue++;
	      }
	    }
	    imageCursor.close();
	    return returnValue;
	  }


	  private void updateUI()
	  {
	    mListAdapter = new ImageAdapter (this, R.layout.image_cell, mThumbImageInfoList);
	    mGvImageList.setAdapter(mListAdapter);
	  }
	  
	  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	  {}


	  public void onScrollStateChanged(AbsListView view, int scrollState)
	  {
	    switch (scrollState)
	    {
	      case OnScrollListener.SCROLL_STATE_IDLE:
	        mBusy = false;
	        mListAdapter.notifyDataSetChanged();
	        break;
	      case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
	        mBusy = true;
	        break;
	      case OnScrollListener.SCROLL_STATE_FLING:
	        mBusy = true;
	        break;
	    }
	  }


	  @Override
	  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
	  {
	    ImageAdapter adapter = (ImageAdapter) arg0.getAdapter();
	    ThumbImageInfo rowData = (ThumbImageInfo)adapter.getItem(position);
	    boolean curCheckState = rowData.getCheckedState();
	    
	    rowData.setCheckedState(!curCheckState);
	    
	    mThumbImageInfoList.set(position, rowData);
	    adapter.notifyDataSetChanged();
	  }
	  
	  // ***************************************************************************************** //
	  // Image Adapter Class 
	  // ***************************************************************************************** //
	  static class ImageViewHolder
	  {
	    ImageView ivImage;
	  }
	  
	  private class ImageAdapter extends BaseAdapter
	  {
	    static final int VISIBLE = 0x00000000; 
	    static final int INVISIBLE = 0x00000004; 
	    private Context mContext;
	    private int mCellLayout;
	    private LayoutInflater mLiInflater;
	    private ArrayList<ThumbImageInfo> mThumbImageInfoList;
	    @SuppressWarnings("unchecked")
	    private LRUCache mCache;
	    
	    public ImageAdapter(Context c, int cellLayout, ArrayList<ThumbImageInfo> thumbImageInfoList)
	    {
	      mContext = c;
	      mCellLayout = cellLayout;
	      mThumbImageInfoList = thumbImageInfoList;
	      
	      mLiInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


	      mCache = new LRUCache<String, Bitmap>(30);
	    }
	    
	    public int getCount()
	    {
	      return mThumbImageInfoList.size();
	    }

	    public Object getItem(int position)
	    {
	      return mThumbImageInfoList.get(position);
	    }

	    public long getItemId(int position)
	    {
	      return position;
	    }
	    
	    @SuppressWarnings("unchecked")
	    public View getView(int position, View convertView, ViewGroup parent)
	    {
	      if (convertView == null)
	      {
	        convertView = mLiInflater.inflate(mCellLayout, parent, false);
	        ImageViewHolder holder = new ImageViewHolder();
	        
	        holder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
	        
	        convertView.setTag(holder);
	      }

	      final ImageViewHolder holder = (ImageViewHolder) convertView.getTag();
	      
	      if (!mBusy)
	      {
	        try
	        {
	          String path = ((ThumbImageInfo) mThumbImageInfoList.get(position)).getData();
	          Bitmap bmp = (Bitmap) mCache.get(path);



	          if (bmp != null)
	          {
	            holder.ivImage.setImageBitmap(bmp);
	          }

	          else
	          {
	            BitmapFactory.Options option = new BitmapFactory.Options();
	            
	            if (new File(path).length() > 100000)
	              option.inSampleSize = 10;
	            else
	              option.inSampleSize = 2;
	            
	            bmp = BitmapFactory.decodeFile(path, option);
	            holder.ivImage.setImageBitmap(bmp);  
	            
	            mCache.put(path, bmp);
	          }
	          
	          holder.ivImage.setVisibility(VISIBLE);
	          setProgressBarIndeterminateVisibility(false);
	        }
	        catch (Exception e)
	        {
	          e.printStackTrace();
	          setProgressBarIndeterminateVisibility(false);
	        }
	      }
	      else
	      {
	        setProgressBarIndeterminateVisibility(true);
	        holder.ivImage.setVisibility(INVISIBLE);
	      }
	        
	      return convertView;
	    }
	  }
	  // ***************************************************************************************** //
	  // Image Adapter Class End
	  // ***************************************************************************************** //
	  
	  // ***************************************************************************************** //
	  // AsyncTask Class 
	  // ***************************************************************************************** //
	  private class DoFindImageList extends AsyncTask<String, Integer, Long>
	  {
	    @Override
	    protected void onPreExecute()
	    {
	      mLoagindDialog = ProgressDialog.show(UploadActivity.this, null, "Loading...", true, true);
	      super.onPreExecute();
	    }
	    
	    @Override
	    protected Long doInBackground(String... arg0)
	    {
	      long returnValue = 0;
	      returnValue = findThumbList();
	      return returnValue;
	    }

	    @Override
	    protected void onPostExecute(Long result)
	    {
	      updateUI();
	      mLoagindDialog.dismiss();
	    }

	    @Override
	    protected void onCancelled()
	    {
	      super.onCancelled();
	    }
	  }
	  // ***************************************************************************************** //
	  // AsyncTask Class End
	  // ***************************************************************************************** //
	  
}
