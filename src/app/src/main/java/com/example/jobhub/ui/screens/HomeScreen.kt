package com.example.jobhub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onJobClick: (Int) -> Unit
) {
    val homeState by viewModel.homeState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Listings", fontWeight = FontWeight.Bold, color = TextPrimaryLight) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                ),
                modifier = Modifier.shadow(2.dp)
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (homeState) {
                is HomeState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BluePrimary
                    )
                }
                is HomeState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (homeState as HomeState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(
                            onClick = { viewModel.fetchJobs() },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                        ) {
                            Text("Retry")
                        }
                    }
                }
                is HomeState.Success -> {
                    val jobs = (homeState as HomeState.Success).jobs
                    if (jobs.isEmpty()) {
                        Text(
                            text = "No jobs available at the moment.",
                            color = TextSecondaryLight,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(jobs) { job ->
                                JobCard(job = job, onClick = { onJobClick(job.id) })
                            }
                        }
                    }
                }
            }
        }
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
