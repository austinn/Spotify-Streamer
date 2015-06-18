package com.refect.spotifystreamer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.refect.spotifystreamer.R;
import com.refect.spotifystreamer.listeners.OnRecyclerViewItemClickListener;
import com.refect.spotifystreamer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;


/**
 *
 * @author Austin
 */
public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> implements View.OnClickListener {

	private List<Track> models;
	private OnRecyclerViewItemClickListener<Track> itemClickListener;
	private static Context mContext;
	private int lastAnimatedPosition = -1;
	private static final int ANIMATED_ITEMS_COUNT = 7;
	private boolean animateItems = false;

	public TrackAdapter(Context context) {
		this.models = new ArrayList<>();
		this.mContext = context;
	}

	public TrackAdapter(List<Track> models, Context context) {
		this.models = models;
		this.mContext = context;
	}

	public void setModels(List<Track> models) {
		this.models = models;
		notifyDataSetChanged();
	}

	public void clear() {
		this.models.clear();
		lastAnimatedPosition = -1;
		notifyDataSetChanged();
	}

	private void runEnterAnimation(View view, int position) {

		if (position > lastAnimatedPosition) {
			lastAnimatedPosition = position;
			view.setTranslationY(Utils.getScreenHeight(mContext));
			view.animate()
					.translationY(0)
					.setInterpolator(new DecelerateInterpolator(3.f))
					.setDuration((position*100) + 1000)
					.start();
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_track, viewGroup, false);
		v.setOnClickListener(this);		
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, int i) {
		runEnterAnimation(viewHolder.itemView, i);
		final Track model = models.get(i);
		viewHolder.itemView.setTag(model);

		viewHolder.title.setText(model.name);
		viewHolder.artist.setText(model.artists.get(0).name);
	}

	@Override
	public int getItemCount() {
		return models == null ? 0 : models.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView title;
		public TextView artist;

		public ViewHolder(View itemView) {
			super(itemView);
			title = (TextView) itemView.findViewById(R.id.tv_track_name);
			artist = (TextView) itemView.findViewById(R.id.tv_track_artist);
		}

	}

	public void add(Track item, int position) {
		models.add(position, item);
		notifyItemInserted(position);
	}

	public void remove(Track item) {
		int position = models.indexOf(item);
		models.remove(position);
		notifyItemRemoved(position);
	}

	public void setOnItemClickListener(OnRecyclerViewItemClickListener<Track> listener) {
		this.itemClickListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (itemClickListener != null) {
			Track model = (Track) v.getTag();
			itemClickListener.onItemClick(v, model);
		}
	}
}