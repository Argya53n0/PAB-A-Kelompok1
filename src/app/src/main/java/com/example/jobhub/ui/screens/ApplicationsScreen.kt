package com.example.jobhub.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobhub.data.model.Application
import com.example.jobhub.ui.theme.BackgroundLight
import com.example.jobhub.ui.theme.BluePrimary
import com.example.jobhub.ui.theme.TextPrimaryLight
import com.example.jobhub.ui.theme.TextSecondaryLight
import com.example.jobhub.ui.viewmodel.ApplicationsState
import com.example.jobhub.ui.viewmodel.ApplicationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationsScreen(
    viewModel: ApplicationsViewModel,
    onBackClick: () -> Unit,
    onApplicationClick: (Int) -> Unit
) {
    val state by viewModel.applicationsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchApplications()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Status Lamaran", fontWeight = FontWeight.Bold, color = BluePrimary) },
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
                is ApplicationsState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BluePrimary
                    )
                }
                is ApplicationsState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (state as ApplicationsState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(
                            onClick = { viewModel.fetchApplications() },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is ApplicationsState.Success -> {
                    val applications = (state as ApplicationsState.Success).applications
                    
                    if (applications.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Belum ada lamaran terkirim.", color = TextSecondaryLight)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(applications) { application ->
                                ApplicationCard(application = application, onClick = { onApplicationClick(application.jobListingId) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationCard(application: Application, onClick: () -> Unit) {
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
                text = application.job_listing?.title ?: "Lowongan Tidak Diketahui",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimaryLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = application.job_listing?.company?.name ?: "-",
                fontSize = 14.sp,
                color = TextSecondaryLight
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tanggal: ${application.createdAt?.take(10) ?: "-"}",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
                
                val statusColor = when(application.status.lowercase()) {
                    "accepted", "diterima" -> Color(0xFF4CAF50)
                    "rejected", "ditolak" -> Color(0xFFF44336)
                    else -> Color(0xFFFF9800)
                }
                
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = application.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
