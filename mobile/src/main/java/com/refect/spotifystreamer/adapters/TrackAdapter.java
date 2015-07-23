package com.refect.spotifystreamer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.refect.spotifystreamer.R;
import com.refect.spotifystreamer.listeners.OnRecyclerViewItemClickListener;
import com.refect.spotifystreamer.models.TrackModel;
import com.refect.spotifystreamer.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;


/**
 *
 * @author Austin
 */
public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> implements View.OnClickListener {

	private ArrayList<TrackModel> models;
	private OnRecyclerViewItemClickListener<TrackModel> itemClickListener;
	private static Context mContext;
	private int lastAnimatedPosition = -1;

	public TrackAdapter(Context context) {
		this.models = new ArrayList<>();
		this.mContext = context;
	}

	public TrackAdapter(ArrayList<TrackModel> models, Context context) {
		this.models = models;
		this.mContext = context;
	}

	public void setModels(ArrayList<TrackModel> models) {
		this.models = models;
		notifyDataSetChanged();
	}

	public ArrayList<TrackModel> getModels() {
		return this.models;
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
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_track_simple, viewGroup, false);
		v.setOnClickListener(this);		
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, int i) {
		runEnterAnimation(viewHolder.itemView, i);
		final TrackModel model = models.get(i);
		viewHolder.itemView.setTag(model);

		viewHolder.title.setText(i + "");
		viewHolder.artist.setText(model.getTitle());

//		Picasso.with(mContext).load(model.getUrl())
//				.into(viewHolder.image);
	}

	@Override
	public int getItemCount() {
		return models == null ? 0 : models.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView title;
		public TextView artist;
		public ImageView image;

		public ViewHolder(View itemView) {
			super(itemView);
			title = (TextView) itemView.findViewById(R.id.tv_track_name);
			artist = (TextView) itemView.findViewById(R.id.tv_track_artist);
//			image = (ImageView) itemView.findViewById(R.id.iv_album_image);
		}

	}

	public void add(TrackModel item, int position) {
		models.add(position, item);
		notifyItemInserted(position);
	}

	public void remove(TrackModel item) {
		int position = models.indexOf(item);
		models.remove(position);
		notifyItemRemoved(position);
	}

	public void setOnItemClickListener(OnRecyclerViewItemClickListener<TrackModel> listener) {
		this.itemClickListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (itemClickListener != null) {
			TrackModel model = (TrackModel) v.getTag();
			itemClickListener.onItemClick(v, model);
		}
	}
}