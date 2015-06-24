package com.refect.spotifystreamer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.refect.spotifystreamer.R;
import com.refect.spotifystreamer.listeners.OnRecyclerViewItemClickListener;
import com.refect.spotifystreamer.models.NavigationModel;
import com.refect.spotifystreamer.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> implements View.OnClickListener {

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private static final int ANIMATED_ITEMS_COUNT = 7;

    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;
    private boolean animateItems = false;

	public void setModels(List<NavigationModel> models) {
		this.models = models;
		lastAnimatedPosition = -1;
		this.notifyDataSetChanged();
	}

	private List<NavigationModel> models;
	private OnRecyclerViewItemClickListener<NavigationModel> itemClickListener;
	private static Context mContext;
	private int lastPosition = -1;

	public NavigationDrawerAdapter(Context context, List<NavigationModel> models) {
		this.models = models;
		this.mContext = context;
	}

    public NavigationDrawerAdapter(Context context) {
        this.mContext = context;
        models = new ArrayList<>();
    }

    private void runEnterAnimation(View view, int position) {

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationX(-Utils.getScreenWidth(mContext));
            view.animate()
                    .translationX(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration((position*100) + 1000)
                    .start();
        }
    }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_navigation, viewGroup, false);
		v.setOnClickListener(this);

		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int i) {
        runEnterAnimation(viewHolder.itemView, i);
		final NavigationModel model = models.get(i);

		viewHolder.itemView.setTag(model);
        viewHolder.name.setText(model.getName());
		viewHolder.image.setImageResource(model.getResId());
	}

	@Override
	public int getItemCount() {
		return models == null ? 0 : models.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView name;
        public ImageView image;

		public ViewHolder(View itemView) {
			super(itemView);
			name = (TextView) itemView.findViewById(R.id.tv_navigation_title);
			image = (ImageView) itemView.findViewById(R.id.iv_navigation_image);
		}

	}

	public void add(NavigationModel item, int position) {
		models.add(position, item);
		notifyItemInserted(position);
	}

    public void add(NavigationModel item) {
        models.add(item);
        notifyDataSetChanged();
    }

	public void remove(NavigationModel item) {
		int position = models.indexOf(item);
		models.remove(position);
		notifyItemRemoved(position);
	}

	public void setOnItemClickListener(OnRecyclerViewItemClickListener<NavigationModel> listener) {
		this.itemClickListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (itemClickListener != null) {
			NavigationModel model = (NavigationModel) v.getTag();
			itemClickListener.onItemClick(v, model);
		}
	}


}