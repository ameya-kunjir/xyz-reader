package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

import java.text.SimpleDateFormat;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailArticleActivityFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "DetailArticleFragment";
    private CollapsingToolbarLayout mCollapseToolbar;
    private long mSelectedItemId;
    private Cursor mCursor;
    private View mRootView;
    private Toolbar mToolbar;
    private ImageView mImageView;
    private String mTransitionName;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");


    public DetailArticleActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail_article, container, false);

        mRootView = rootView;

        mCollapseToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar_layout);

        if (savedInstanceState == null) {
            if (intent != null && intent.getData() != null) {
                mSelectedItemId = ItemsContract.Items.getItemId(intent.getData());
            }
        }

        mImageView = (ImageView) rootView.findViewById(R.id.article_img);

        rootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        mToolbar = (Toolbar) rootView.findViewById(R.id.detail_view_toolbar);

        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ArticleListActivity.class);
                    startActivity(intent);
                }
            });
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mSelectedItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCursor = data;
        mCursor.moveToFirst();

        TextView bodyView = (TextView) mRootView.findViewById(R.id.article_detail_body);

        if (mCursor != null) {

            bodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));

            mCollapseToolbar.setTitle(mCursor.getString(ArticleLoader.Query.TITLE));

            bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY).replaceAll("(\r\n|\n)", "<br />")));

            final ImageView thumbnailView = (ImageView) mRootView.findViewById(R.id.article_img);
            Glide.with(getActivity())
                    .load(mCursor.getString(ArticleLoader.Query.THUMB_URL))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .listener(new RequestListener<String, GlideDrawable>() {
                                  @Override
                                  public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                      return false;
                                  }

                                  @Override
                                  public boolean onResourceReady(GlideDrawable resource, String model,
                                                                 Target<GlideDrawable> target,
                                                                 boolean isFromMemoryCache, boolean isFirstResource) {
                                      Bitmap bitmap = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
                                      Palette palette = Palette.generate(bitmap);
                                      int defaultColor = 0xFF333333;
                                      int color = palette.getDarkMutedColor(defaultColor);
                                      thumbnailView.setBackgroundColor(color);
                                      return false;
                                  }
                              }
                    ).into(thumbnailView);

        } else {
            mRootView.setVisibility(View.GONE);
            bodyView.setText("N/A");
        }

        if (mCursor != null) {
            mCursor.close();
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }

}
