package com.example.submissiondicoding.api
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.submissiondicoding.api.response.StoryItem
import com.example.submissiondicoding.api.retrofit.ApiService


class StoryPagingSource(private val token: String, private val apiService: ApiService) : PagingSource<Int, StoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStoryPaging(token, position, params.loadSize)
            val stories = responseData.listStory

            Log.d("Paging Source", "Panjang data story ${stories.size}")
            LoadResult.Page(
                data = stories,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (stories.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}