package com.newsblur.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.newsblur.R;
import com.newsblur.activity.Reading;
import com.newsblur.database.DatabaseConstants;
import com.newsblur.database.FeedProvider;
import com.newsblur.view.ItemViewBinder;

public class ItemListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {
	
	private static final String TAG = "itemListFragment";
	private ContentResolver contentResolver;
	private String feedId;
	public static int ITEMLIST_LOADER = 0x01;
	private SimpleCursorAdapter adapter;

	public ItemListFragment(final String feedId) {
		this.feedId = feedId;
	}
	
	public ItemListFragment() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public static ItemListFragment newInstance(final String feedId) {
		return new ItemListFragment(feedId);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_itemlist, null);
		ListView itemList = (ListView) v.findViewById(R.id.itemlistfragment_list);
		
		contentResolver = getActivity().getContentResolver();
		Uri uri = FeedProvider.STORIES_URI.buildUpon().appendPath(feedId).build();
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		
		String[] groupFrom = new String[] { DatabaseConstants.STORY_TITLE, DatabaseConstants.STORY_AUTHORS, DatabaseConstants.STORY_READ, DatabaseConstants.STORY_DATE, DatabaseConstants.STORY_INTELLIGENCE_AUTHORS };
		int[] groupTo = new int[] { R.id.row_item_title, R.id.row_item_author, R.id.row_item_title, R.id.row_item_date, R.id.row_item_sidebar };

		getLoaderManager().initLoader(ITEMLIST_LOADER , null, this);
				
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.row_item, cursor, groupFrom, groupTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
		adapter.setViewBinder(new ItemViewBinder());
		itemList.setAdapter(adapter);
		itemList.setOnItemClickListener(this);
		
		return v;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		Uri uri = FeedProvider.STORIES_URI.buildUpon().appendPath(feedId).build();
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, null, null, null, null);
	    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.d(TAG, "Load finished: " + cursor.getCount());
		adapter.swapCursor(cursor);
	}
	
	public void updated() {
		getLoaderManager().restartLoader(ITEMLIST_LOADER , null, this);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.d(TAG, "Loader reset");
		adapter.notifyDataSetInvalidated();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent i = new Intent(getActivity(), Reading.class);
		i.putExtra(Reading.EXTRA_FEED, feedId);
		i.putExtra(Reading.EXTRA_POSITION, position);
		startActivity(i);
	}

	

}
