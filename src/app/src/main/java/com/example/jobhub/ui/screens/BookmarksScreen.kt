package com.example.jobhub.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobhub.data.model.Bookmark
import com.example.jobhub.ui.theme.BackgroundLight
import com.example.jobhub.ui.theme.BluePrimary
import com.example.jobhub.ui.theme.TextPrimaryLight
import com.example.jobhub.ui.theme.TextSecondaryLight
import com.example.jobhub.ui.viewmodel.BookmarksState
import com.example.jobhub.ui.viewmodel.BookmarksViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel,
    onBackClick: () -> Unit,
    onJobClick: (Int) -> Unit
) {
    val state by viewModel.bookmarksState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchBookmarks()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lowongan Tersimpan", fontWeight = FontWeight.Bold, color = BluePrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryLight)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is BookmarksState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BluePrimary
                    )
                }
                is BookmarksState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (state as BookmarksState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(
                            onClick = { viewModel.fetchBookmarks() },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is BookmarksState.Success -> {
                    val bookmarks = (state as BookmarksState.Success).bookmarks

                    if (bookmarks.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkRemove,
                                contentDescription = null,
                                tint = TextSecondaryLight.copy(alpha = 0.4f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Belum ada lowongan tersimpan.", color = TextSecondaryLight)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(bookmarks) { bookmark ->
                                BookmarkCard(
                                    bookmark = bookmark,
                                    onClick = { onJobClick(bookmark.jobListingId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookmarkCard(bookmark: Bookmark, onClick: () -> Unit) {
    val job = bookmark.job_listing

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = job?.title ?: "Lowongan Tidak Diketahui",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimaryLight,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = job?.company?.name ?: "-",
                fontSize = 14.sp,
                color = BluePrimary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Location
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondaryLight
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = job?.location ?: "-",
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )
                }
                // Employment type
                if (job?.employment_type != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Work,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = TextSecondaryLight
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = job.employment_type.replaceFirstChar { it.uppercase() },
                            fontSize = 12.sp,
                            color = TextSecondaryLight
                        )
                    }
                }
            }

            // Salary
            if (job?.salary_min != null || job?.salary_max != null) {
                Spacer(modifier = Modifier.height(8.dp))
                val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                val salaryText = when {
                    job.salary_min != null && job.salary_max != null ->
                        "${fmt.format(job.salary_min)} - ${fmt.format(job.salary_max)}"
                    job.salary_min != null -> "Min ${fmt.format(job.salary_min)}"
                    else -> "Maks ${fmt.format(job.salary_max)}"
                }
                Text(
                    text = salaryText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}
