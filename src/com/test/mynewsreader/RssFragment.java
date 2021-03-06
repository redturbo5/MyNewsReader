/** Aplikasi New Reader
 * Di Buat oleh Jonathan Hindharta**/
//class fragment untuk menampilkan list Rss Feed
package com.test.mynewsreader;


import java.util.List;


import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class RssFragment extends Fragment implements OnItemClickListener {

	private ProgressBar progressBar;
	private ListView listView;
	private View view;
	Context context;
	public static final String ITEMS = "Item";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_layout_inside, container, false);
			progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
			listView = (ListView) view.findViewById(R.id.listView);
			listView.setOnItemClickListener(this);
			startService();
		} else {
			// If we are returning from a configuration change:
			// "view" is still attached to the previous view hierarchy
			// so we need to remove it and re-attach it to the current one
			ViewGroup parent = (ViewGroup) view.getParent();
			parent.removeView(view);
		}
		return view;
	}

	private void startService() {
		Intent intent = new Intent(getActivity(), RssService.class);
		intent.putExtra(RssService.RECEIVER, resultReceiver);
		getActivity().startService(intent);
	}

	/**
	 * Once the {@link RssService} finishes its task, the result is sent to this
	 * ResultReceiver.
	 */
	private final ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
		@SuppressWarnings("unchecked")
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			progressBar.setVisibility(View.GONE);
			List<RssItem> items = (List<RssItem>) resultData.getSerializable(RssService.ITEMS);
			
			if (items != null) {
				RssAdapter adapter = new RssAdapter(getActivity(), items);
				listView.setAdapter(adapter);
			} else {
				Toast.makeText(getActivity(), "An error occured while downloading the rss feed.",
						Toast.LENGTH_LONG).show();
			}
		};
	};
	//Menentukan Aksi ketika salah satu list dipilih
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		RssAdapter adapter = (RssAdapter) parent.getAdapter();
		RssItem item = (RssItem) adapter.getItem(position);
		
		//Mendapatkan data url, judul, deskripsi dari list rss yang dipilih
		Uri uri = Uri.parse(item.getLink());
		String desc = item.getDescription();
		String judul = item.getTitle();
		String url = String.valueOf(uri);
		//Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		Bundle dt = new Bundle();
        dt.putString("urls", url);	
        dt.putString("descs", desc);	
        dt.putString("juduls", judul);
		
        //Pindah ke fragment RssDetailFragment untuk menampilkan detail berita
	    FragmentManager fragmentManager = ((FragmentActivity) getActivity()).getSupportFragmentManager();
	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	    RssDetailFragment rssdf = new RssDetailFragment();
	    rssdf.setArguments(dt);
	    fragmentTransaction.replace(R.id.fragment_container, rssdf);
	    fragmentTransaction.commit();
		
		
        
	}
	
	

	}
