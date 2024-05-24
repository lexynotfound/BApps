package com.raihanardila.bapps.core.data.viewmodel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.raihanardila.bapps.core.data.adapter.BFeedAdapter.Companion.DIFF_CALLBACK
import com.raihanardila.bapps.core.data.local.repository.BMainRepository
import com.raihanardila.bapps.utils.Dummys
import com.raihanardila.bapps.utils.MainTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class BMainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainTestRule()

    @Mock
    private lateinit var repository: BMainRepository
    private lateinit var viewModel: BMainViewModel
    private lateinit var logMock: MockedStatic<Log>

    @Before
    fun setUp() {
        logMock = mockStatic(Log::class.java)  // Start mocking
        logMock.`when`<Boolean> {
            Log.isLoggable(
                anyString(),
                anyInt()
            )
        }.thenReturn(true)
        viewModel = BMainViewModel(repository)
    }

    @After
    fun tearDown() {
        logMock.close()
    }

    @Test
    fun `when Get Stories first successfully loaded`() = runTest {
        whenever(
            repository.getStories(
                1,
                20
            )
        ).thenReturn(flowOf(PagingData.from(Dummys.generateResponse())))
        val differ = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = NoopListCallback,
            workerDispatcher = mainCoroutineRule.dispatcher
        )

        val actualStories = viewModel.storiesFlow.first()
        differ.submitData(actualStories)

        // Assert that the data is not null
        Assert.assertNotNull(differ.snapshot())

        // Assert that the number of items matches the expected number
        Assert.assertEquals(Dummys.generateResponse().size, differ.snapshot().size)

        // Assert that the first item matches the expected item
        val expectedFirstItem = Dummys.generateResponse().first()
        val actualFirstItem = differ.snapshot().first()
        Assert.assertEquals(expectedFirstItem, actualFirstItem)
    }

    @Test
    fun `when Get Stories amount of data is matching as expected`() = runTest {
        val dummyStory = Dummys.generateResponse()
        val pagingData = PagingData.from(dummyStory)
        whenever(repository.getStories(1, 20)).thenReturn(flowOf(pagingData))

        val actualStory = viewModel.storiesFlow.first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = NoopListCallback,
            workerDispatcher = mainCoroutineRule.dispatcher
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
    }

    @Test
    fun `when Get Stories returns empty`() = runTest {
        whenever(repository.getStories(1, 20)).thenReturn(flowOf(PagingData.empty()))
        val differ = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = NoopListCallback,
            workerDispatcher = mainCoroutineRule.dispatcher
        )

        val actualStories = viewModel.storiesFlow.first()
        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertTrue(differ.snapshot().isEmpty())
    }

    @Test
    fun `when Get Stories fails with an exception`() = runTest {
        val exception = RuntimeException("Failed to fetch data")
        whenever(repository.getStories(1, 20)).thenThrow(exception)

        try {
            viewModel.storiesFlow.first()
            Assert.fail("Expected an exception to be thrown")
        } catch (e: Exception) {
            Assert.assertEquals("Failed to fetch data", e.message)
        }
    }
}

val NoopListCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
