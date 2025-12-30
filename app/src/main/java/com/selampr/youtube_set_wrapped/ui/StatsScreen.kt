package com.selampr.youtube_set_wrapped.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.selampr.youtube_set_wrapped.ui.StatsViewModel
import com.selampr.youtube_set_wrapped.domain.model.VideoStat

@Composable
fun StatsScreen(vm: StatsViewModel) {
    val stats = vm.stats

    Column(Modifier.padding(16.dp)) {
        Text("Your Statistics:")

        LazyColumn {
            items(stats) { item ->
                Text("${item.title} → ${item.count}", color = Color.White, modifier = Modifier.padding(bottom = 3.dp))
            }
        }
    }
}


