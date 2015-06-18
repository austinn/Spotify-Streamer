package com.refect.spotifystreamer.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.refect.spotifystreamer.R;
import com.refect.spotifystreamer.listeners.OnRecyclerViewItemClickListener;
import com.refect.spotifystreamer.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;


/**
 *
 * @author Austin
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> implements View.OnClickListener {

	private List<Artist> models;
	private OnRecyclerViewItemClickListener<Artist> itemClickListener;
	private static Context mContext;
	private int lastAnimatedPosition = -1;
	private static final int ANIMATED_ITEMS_COUNT = 7;
	private boolean animateItems = false;

	public ArtistAdapter(Context context) {
		this.models = new ArrayList<>();
		this.mContext = context;
	}

	public ArtistAdapter(List<Artist> models, Context context) {
		this.models = models;
		this.mContext = context;
	}

	public void setModels(List<Artist> models) {
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
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_artist, viewGroup, false);
		v.setOnClickListener(this);		
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, int i) {
		runEnterAnimation(viewHolder.itemView, i);
		final Artist model = models.get(i);
		viewHolder.itemView.setTag(model);
		viewHolder.name.setText(model.name);

		RelativeLayout.LayoutParams params =
				new RelativeLayout.LayoutParams(Utils.getScreenWidth(mContext)/2, Utils.getScreenWidth(mContext)/2);
		viewHolder.image.setLayoutParams(params);

		if(model.images.size() > 0) {
			Picasso.with(mContext)
					.load(model.images.get(0).url)
					.into(viewHolder.image, new Callback() {
						@Override
						public void onSuccess() {
							Bitmap bitmap = ((BitmapDrawable)viewHolder.image.getDrawable()).getBitmap();
							Palette p = Palette.from(bitmap).generate();

							viewHolder.background.setBackgroundColor(p.getDarkMutedColor(Color.parseColor("#141414")));
						}

						@Override
						public void onError() {

						}
					});
		}
	}

	@Override
	public int getItemCount() {
		return models == null ? 0 : models.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView name;
		public TextView genre;
		public ImageView image;
		public LinearLayout background;

		public ViewHolder(View itemView) {
			super(itemView);
			name = (TextView) itemView.findViewById(R.id.tv_artist_name);
			genre = (TextView) itemView.findViewById(R.id.tv_artist_genre);
			image = (ImageView) itemView.findViewById(R.id.iv_artist_image);
			background = (LinearLayout) itemView.findViewById(R.id.ll_artist_background);
		}

	}

	public void add(Artist item, int position) {
		models.add(position, item);
		notifyItemInserted(position);
	}

	public void remove(Artist item) {
		int position = models.indexOf(item);
		models.remove(position);
		notifyItemRemoved(position);
	}

	public void setOnItemClickListener(OnRecyclerViewItemClickListener<Artist> listener) {
		this.itemClickListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (itemClickListener != null) {
			Artist model = (Artist) v.getTag();
			itemClickListener.onItemClick(v, model);
		}
	}
}