package com.refect.spotifystreamer.adapters;

import android.app.Activity;
import android.graphics.Picture;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.refect.spotifystreamer.R;
import com.refect.spotifystreamer.models.AlbumModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends PagerAdapter {

    private List<AlbumModel> models;
    private Activity mActivity;

    public AlbumAdapter(Activity mActivity) {
        this.mActivity = mActivity;
        models = new ArrayList<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View page = mActivity.getLayoutInflater().inflate(R.layout.view_item_album, container, false);
        ImageView ivAlbumImage = (ImageView) page.findViewById(R.id.iv_album_image);

        Picasso.with(mActivity).load(models.get(position).getUrl())
                .into(ivAlbumImage);

        container.addView(page);

        return(page);
    }

    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return(view == object);
    }

    public void setModels(List<AlbumModel> models) {
        this.models = models;
        this.notifyDataSetChanged();
    }

    public void clear() {
        this.models.clear();
    }

    public AlbumModel getItemAt(int pos) {
        return models.get(pos);
    }
}
