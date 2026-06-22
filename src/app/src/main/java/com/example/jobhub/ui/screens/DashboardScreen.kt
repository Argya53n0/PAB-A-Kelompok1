package com.example.jobhub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobhub.data.model.Application
import com.example.jobhub.ui.theme.BackgroundLight
import com.example.jobhub.ui.theme.BluePrimary
import com.example.jobhub.ui.theme.TextPrimaryLight
import com.example.jobhub.ui.theme.TextSecondaryLight
import com.example.jobhub.ui.viewmodel.DashboardState
import com.example.jobhub.ui.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Locale

import androidx.compose.runtime.LaunchedEffect

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.HorizontalDivider as Divider
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.ArrowForward

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onApplicationClick: (Int) -> Unit,
    onBrowseJobsClick: () -> Unit,
    onLogout: () -> Unit
) {
    val dashboardState by viewModel.dashboardState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchDashboardIfNeeded()
    }

    Scaffold(
        containerColor = BackgroundLight
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (dashboardState) {
                is DashboardState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BluePrimary
                    )
                }
                is DashboardState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (dashboardState as DashboardState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(
                            onClick = { viewModel.fetchDashboard() },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is DashboardState.Success -> {
                    val state = dashboardState as DashboardState.Success
                    val data = state.data
                    val user = state.user

                    LazyColumn(
                        contentPadding = PaddingValues(24.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Header Section
                        item {
                            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                                Text(
                                    text = "Dashboard Pelamar",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A237E) // Dark Blue
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Selamat datang kembali, pantau status lamaranmu di sini.",
                                    fontSize = 14.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }

                        // Stats Section
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                StatCard(
                                    title = "Total Dilamar",
                                    count = data.totalApplications,
                                    icon = Icons.Default.Description,
                                    iconBgColor = Color(0xFFE8EAF6),
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    title = "Menunggu",
                                    count = data.waitingApplications,
                                    icon = Icons.Default.HourglassEmpty,
                                    iconBgColor = Color(0xFFFFF9C4),
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    title = "Diterima",
                                    count = data.acceptedApplications,
                                    icon = Icons.Default.Celebration,
                                    iconBgColor = Color(0xFFE8F5E9),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        // Recent Applications Section
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Lamaran Terbaru",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimaryLight
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable { onBrowseJobsClick() }
                                        ) {
                                            Text(
                                                text = "Lihat Semua",
                                                fontSize = 12.sp,
                                                color = BluePrimary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = BluePrimary
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    // Table Header
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        TableHeaderItem("LOWONGAN", Modifier.weight(1.5f))
                                        TableHeaderItem("PERUSAHAAN", Modifier.weight(1.5f))
                                        TableHeaderItem("TANGGAL LAMAR", Modifier.weight(1.5f))
                                        TableHeaderItem("STATUS", Modifier.weight(1f))
                                    }
                                    
                                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))

                                    if (data.recentApplications.isEmpty()) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 40.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Belum ada lamaran terkirim.",
                                                color = TextSecondaryLight,
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Cari Lowongan Sekarang",
                                                color = BluePrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.clickable { onBrowseJobsClick() }
                                            )
                                        }
                                    } else {
                                        data.recentApplications.forEach { application ->
                                            ApplicationRow(application, onClick = { onApplicationClick(application.jobListingId) })
                                            Divider(color = Color(0xFFF5F5F5))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableHeaderItem(text: String, modifier: Modifier) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextSecondaryLight.copy(alpha = 0.6f),
        modifier = modifier
    )
}

@Composable
fun ApplicationRow(application: Application, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = application.job_listing?.title ?: "-",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimaryLight,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = application.job_listing?.company?.name ?: "-",
            fontSize = 13.sp,
            color = TextSecondaryLight,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = application.createdAt?.take(10) ?: "-",
            fontSize = 13.sp,
            color = TextSecondaryLight,
            modifier = Modifier.weight(1.5f)
        )
        
        val statusColor = when(application.status.lowercase()) {
            "accepted", "diterima" -> Color(0xFF4CAF50)
            "rejected", "ditolak" -> Color(0xFFF44336)
            else -> Color(0xFF9E9E9E)
        }
        
        Text(
            text = application.status.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String, 
    count: Int, 
    icon: ImageVector, 
    iconBgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = iconBgColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = BluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TextSecondaryLight,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = count.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
            }
        }
    }
}
