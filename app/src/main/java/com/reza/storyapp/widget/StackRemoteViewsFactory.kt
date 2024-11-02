package com.reza.storyapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.reza.storyapp.R
import com.reza.storyapp.data.remote.pref.UserPreference
import com.reza.storyapp.data.remote.pref.dataStore
import com.reza.storyapp.data.remote.response.ListStoryItem
import com.reza.storyapp.data.remote.retrofit.ApiService
import com.reza.storyapp.di.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(
    private val context: Context,
) :
    RemoteViewsService.RemoteViewsFactory {

    private val stories = mutableListOf<ListStoryItem>()
    private val storiesBitmap = mutableListOf<Bitmap>()
    private val apiService: ApiService = Injection.provideApiService(context)
    private val userPreference: UserPreference = UserPreference.getInstance(context.dataStore)


    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        runBlocking(Dispatchers.IO) {
            val token = userPreference.getSession().first().token
            if (token != "") {
                try {
                    val result = apiService.getStories()
                    stories.addAll(result.listStory)

                    val bitmap = result.listStory.map {
                        Glide.with(context)
                            .asBitmap()
                            .load(it.photoUrl)
                            .submit()
                            .get()
                    }
                    storiesBitmap.addAll(bitmap)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                stories.clear()
                storiesBitmap.clear()
            }
        }
    }

    override fun onDestroy() {
        stories.clear()
        storiesBitmap.clear()
    }

    override fun getCount(): Int = storiesBitmap.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item).apply {
            if (position < storiesBitmap.size) {
                setImageViewBitmap(R.id.image_view, storiesBitmap[position])
            }

        }

        val extras = bundleOf(
            StoryListWidget.EXTRA_ITEM to position
        )

        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.image_view, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null // Consider adding a placeholder

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 1

    override fun hasStableIds(): Boolean = true

}