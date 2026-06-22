package com.example.jobhub.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobhub.data.model.JobListing
import com.example.jobhub.ui.theme.BackgroundLight
import com.example.jobhub.ui.theme.BluePrimary
import com.example.jobhub.ui.theme.TextPrimaryLight
import com.example.jobhub.ui.theme.TextSecondaryLight
import com.example.jobhub.ui.viewmodel.ApplyJobState
import com.example.jobhub.ui.viewmodel.JobDetailState
import com.example.jobhub.ui.viewmodel.JobDetailViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    viewModel: JobDetailViewModel,
    jobId: Int,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val jobDetailState by viewModel.jobDetailState.collectAsState()
    val applyJobState by viewModel.applyJobState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var showApplyDialog by remember { mutableStateOf(false) }
    var coverLetter by remember { mutableStateOf("") }
    var selectedCvUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCvFileName by remember { mutableStateOf<String?>(null) }

    // File picker for CV
    val cvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedCvUri = uri
            // Get file name from URI
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        selectedCvFileName = it.getString(nameIndex)
                    }
                }
            }
        }
    }

    // Fetch detail and user profile on load
    LaunchedEffect(jobId) {
        viewModel.fetchJobDetail(jobId)
        viewModel.fetchUserProfile()
    }

    // Handle apply state side effects
    LaunchedEffect(applyJobState) {
        when (applyJobState) {
            is ApplyJobState.Success -> {
                val message = (applyJobState as ApplyJobState.Success).message
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                showApplyDialog = false
                coverLetter = ""
                selectedCvUri = null
                selectedCvFileName = null
                viewModel.resetApplyState()
            }
            is ApplyJobState.Error -> {
                // Keep dialog open, error will be displayed inside dialog
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("JOBHUB", fontWeight = FontWeight.Bold, color = BluePrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = BluePrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Implement share */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = BluePrimary
                        )
                    }
                    IconButton(onClick = { /* TODO: Implement bookmark */ }) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Simpan",
                            tint = BluePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
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
            when (jobDetailState) {
                is JobDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BluePrimary
                    )
                }
                is JobDetailState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (jobDetailState as JobDetailState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(
                            onClick = { viewModel.fetchJobDetail(jobId) },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is JobDetailState.Success -> {
                    val job = (jobDetailState as JobDetailState.Success).job
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Main Scrollable Content
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            // Job Title and Company
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = job.title,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryLight
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = job.company?.name ?: "Perusahaan Tidak Diketahui",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BluePrimary
                                    )
                                    if (job.category != null) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Surface(
                                            color = BluePrimary.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = job.category.name,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BluePrimary,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Job Specifications Details
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    DetailItem(
                                        icon = Icons.Default.LocationOn,
                                        label = "Lokasi",
                                        value = "${job.location} • ${job.work_type?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } ?: "On-site"}"
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    DetailItem(
                                        icon = Icons.Default.Person,
                                        label = "Jenis Pekerjaan",
                                        value = job.employment_type?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } ?: "Full-time"
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Format Salary
                                    val salaryText = if (job.salary_min != null && job.salary_max != null) {
                                        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                                        format.maximumFractionDigits = 0
                                        "${format.format(job.salary_min)} - ${format.format(job.salary_max)}"
                                    } else {
                                        "Gaji tidak dipublikasikan"
                                    }
                                    DetailItem(
                                        icon = Icons.Default.Work,
                                        label = "Estimasi Gaji",
                                        value = salaryText
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Description Section
                            Text(
                                text = "Deskripsi Pekerjaan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Text(
                                    text = job.description,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = TextSecondaryLight,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Requirements Section
                            if (!job.requirements.isNullOrBlank()) {
                                Text(
                                    text = "Persyaratan",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryLight,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Text(
                                        text = job.requirements,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        color = TextSecondaryLight,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // Bottom Sticky Bar for applying
                        Surface(
                            tonalElevation = 8.dp,
                            color = Color.White,
                            modifier = Modifier.shadow(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Button(
                                    onClick = { showApplyDialog = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Text("Lamar Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Apply Job Dialog with CV Upload Support
        if (showApplyDialog) {
            val job = (jobDetailState as? JobDetailState.Success)?.job
            if (job != null) {
                val profileCvPath = userProfile?.jobSeeker?.cvPath ?: userProfile?.jobSeeker?.resume
                val hasProfileCv = profileCvPath != null

                AlertDialog(
                    onDismissRequest = {
                        if (applyJobState != ApplyJobState.Loading) {
                            showApplyDialog = false
                            selectedCvUri = null
                            selectedCvFileName = null
                            viewModel.resetApplyState()
                        }
                    },
                    title = {
                        Text(
                            text = "Lamar ${job.title}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimaryLight
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = "Lengkapi data berikut untuk melamar posisi ini",
                                fontSize = 13.sp,
                                color = TextSecondaryLight,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Cover Letter Field
                            Text(
                                text = "Cover Letter",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimaryLight,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = coverLetter,
                                onValueChange = { coverLetter = it },
                                placeholder = { Text("Ceritakan mengapa Anda cocok untuk posisi ini...", fontSize = 13.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                maxLines = 5,
                                enabled = applyJobState != ApplyJobState.Loading,
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // CV Section
                            Text(
                                text = "CV / Resume",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimaryLight,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            // Profile CV Status
                            if (hasProfileCv && selectedCvUri == null) {
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Menggunakan CV dari Profil",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2E7D32)
                                            )
                                            Text(
                                                text = profileCvPath!!.substringAfterLast("/"),
                                                fontSize = 11.sp,
                                                color = Color(0xFF4CAF50)
                                            )
                                        }
                                    }
                                }
                            } else if (!hasProfileCv && selectedCvUri == null) {
                                Surface(
                                    color = Color(0xFFFFF3E0),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = Color(0xFFE65100),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = "Belum ada CV di profil. Upload CV di bawah ini.",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFE65100),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }

                            // Selected CV file indicator
                            if (selectedCvUri != null) {
                                Surface(
                                    color = BluePrimary.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null,
                                            tint = BluePrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "CV Baru Terpilih",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BluePrimary
                                            )
                                            Text(
                                                text = selectedCvFileName ?: "File terpilih",
                                                fontSize = 11.sp,
                                                color = BluePrimary.copy(alpha = 0.7f)
                                            )
                                        }
                                        // Remove selected CV button
                                        IconButton(
                                            onClick = {
                                                selectedCvUri = null
                                                selectedCvFileName = null
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Hapus CV",
                                                tint = BluePrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Upload CV Button
                            OutlinedButton(
                                onClick = { cvPickerLauncher.launch("application/pdf") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                enabled = applyJobState != ApplyJobState.Loading
                            ) {
                                Icon(
                                    Icons.Default.UploadFile,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = if (selectedCvUri != null) "Ganti CV" else if (hasProfileCv) "Upload CV Lain (Opsional)" else "Upload CV (PDF)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            if (hasProfileCv && selectedCvUri == null) {
                                Text(
                                    text = "Kosongkan jika ingin menggunakan CV dari profil Anda.",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            // Error message
                            if (applyJobState is ApplyJobState.Error) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFFEEBEE),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = (applyJobState as ApplyJobState.Error).message,
                                        color = Color(0xFFC62828),
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.applyJob(
                                    jobId = job.id,
                                    coverLetter = coverLetter,
                                    cvUri = selectedCvUri,
                                    context = if (selectedCvUri != null) context else null
                                )
                            },
                            enabled = applyJobState != ApplyJobState.Loading
                                    && coverLetter.isNotBlank()
                                    && (hasProfileCv || selectedCvUri != null),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (applyJobState == ApplyJobState.Loading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Mengirim...", color = Color.White)
                            } else {
                                Text("Kirim Lamaran", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showApplyDialog = false
                                selectedCvUri = null
                                selectedCvFileName = null
                                viewModel.resetApplyState()
                            },
                            enabled = applyJobState != ApplyJobState.Loading
                        ) {
                            Text("Batal", color = TextSecondaryLight)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = TextSecondaryLight,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondaryLight
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimaryLight
            )
        }
    }
}
