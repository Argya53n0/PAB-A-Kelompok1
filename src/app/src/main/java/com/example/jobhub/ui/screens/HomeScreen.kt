package com.example.jobhub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobhub.data.model.JobListing
import com.example.jobhub.ui.theme.BackgroundLight
import com.example.jobhub.ui.theme.BluePrimary
import com.example.jobhub.ui.theme.TextPrimaryLight
import com.example.jobhub.ui.theme.TextSecondaryLight
import com.example.jobhub.ui.viewmodel.HomeState
import com.example.jobhub.ui.viewmodel.HomeViewModel
import java.text.NumberFormat
import java.util.Locale

import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onJobClick: (Int) -> Unit
) {
    val homeState by viewModel.homeState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Fetch jobs only when this screen is actually displayed
    LaunchedEffect(Unit) {
        viewModel.fetchJobsIfNeeded()
    }

    Scaffold(
        containerColor = BackgroundLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header Hero Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFF))
                        .padding(horizontal = 24.dp, vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Temukan pekerjaan impianmu\nbersama ribuan perusahaan terbaik",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E),
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ribuan lowongan kerja dari perusahaan terpercaya di seluruh Indonesia",
                        fontSize = 14.sp,
                        color = TextSecondaryLight,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // Search Box
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { viewModel.onSearchQueryChange(it) },
                                placeholder = { Text("Posisi, keahlian, atau perusahaan...", fontSize = 14.sp) },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    keyboardController?.hide()
                                    viewModel.performSearch()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Text("Cari loker")
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val categories by viewModel.categories.collectAsState()
                    val selectedCategory by viewModel.selectedCategory.collectAsState()

                    // Category Filter Bar
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            
                            Surface(
                                color = if (isSelected) BluePrimary else Color(0xFFE8EAF6),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.clickable { viewModel.onCategorySelected(category) }
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 13.sp,
                                    color = if (isSelected) Color.White else TextSecondaryLight,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Stats Section (Optional simplified)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatMiniItem("17+", "Lowongan aktif")
                    StatMiniItem("7+", "Perusahaan")
                    StatMiniItem("98rb+", "Pelamar")
                }
            }

            // Job Listings Section
            item {
                Text(
                    text = if (searchQuery.isEmpty()) "Lowongan Terbaru" else "Hasil Pencarian: \"$searchQuery\"",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }

            when (homeState) {
                is HomeState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = BluePrimary)
                        }
                    }
                }
                is HomeState.Error -> {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = (homeState as HomeState.Error).message, color = Color.Red)
                            Button(onClick = { viewModel.fetchJobs() }) { Text("Retry") }
                        }
                    }
                }
                is HomeState.Success -> {
                    val jobs = (homeState as HomeState.Success).jobs
                    if (jobs.isEmpty()) {
                        item {
                            Text(
                                text = "Tidak ada lowongan yang sesuai.",
                                modifier = Modifier.fillMaxWidth().padding(48.dp),
                                textAlign = TextAlign.Center,
                                color = TextSecondaryLight
                            )
                        }
                    } else {
                        items(jobs) { job ->
                            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                                JobCard(job = job, onClick = { onJobClick(job.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatMiniItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, color = BluePrimary, fontSize = 18.sp)
        Text(text = label, fontSize = 12.sp, color = TextSecondaryLight)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobCard(job: JobListing, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = job.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = job.company?.name ?: "Unknown Company",
                fontSize = 14.sp,
                color = BluePrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = TextSecondaryLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${job.location} • ${job.work_type?.capitalize() ?: "On-site"}",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Job Type",
                    tint = TextSecondaryLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = job.employment_type?.capitalize() ?: "Full-time",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Format Salary
            val salaryText = if (job.salary_min != null && job.salary_max != null) {
                val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                format.maximumFractionDigits = 0
                "${format.format(job.salary_min)} - ${format.format(job.salary_max)}"
            } else {
                "Salary not disclosed"
            }
            
            Text(
                text = salaryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight
            )
        }
    }
}
