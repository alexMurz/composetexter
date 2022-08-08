package com.alexmurz.composetexter.ui.message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexmurz.composetexter.viewmodel.MessageListViewModel
import com.alexmurz.composetexter.viewmodel.MessageViewModel
import com.alexmurz.messages.model.MessageChainParent

private inline val MessageChainParent.viewModelKey: String
    get() = "MessageChainParent(${id})"

@Composable
fun MessageList(
    parent: MessageChainParent,
    modifier: Modifier = Modifier,
) {
    val viewModel: MessageListViewModel = viewModel(
        key = parent.viewModelKey,
        initializer = {
            MessageListViewModel(parent)
        }
    )

    MessageList(viewModel, modifier)
}

@Composable
fun MessageList(
    vm: MessageListViewModel,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val messages by vm.messages.collectAsState()

    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize(),
        reverseLayout = true,
    ) {
        items(
            items = messages,
            key = { it.message.id },
            itemContent = {
                ItemView(it)
            }
        )
    }
}

@Composable
private fun LazyItemScope.ItemView(it: MessageViewModel) {
    /**
     * Double Box to align according to fillMaxWidth and limit maxWidth
     */
    Box(
        modifier = Modifier.fillParentMaxWidth(),
    ) {
        val align = if (it.isLeft) {
            Alignment.CenterStart
        } else {
            Alignment.CenterEnd
        }
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(align)
                .padding(all = 4.dp)
        ) {
            MessageView(
                vm = it,
                modifier = Modifier.align(align)
            )
        }
    }
}
