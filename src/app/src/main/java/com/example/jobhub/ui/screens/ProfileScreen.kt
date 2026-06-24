package com.example.jobhub.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jobhub.network.ApiClient
import com.example.jobhub.ui.theme.BackgroundLight
import com.example.jobhub.ui.theme.BluePrimary
import com.example.jobhub.ui.theme.TextPrimaryLight
import com.example.jobhub.ui.theme.TextSecondaryLight
import com.example.jobhub.ui.viewmodel.ProfileState
import com.example.jobhub.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val profileState by viewModel.profileState.collectAsState()
    val isUploadingCv by viewModel.isUploadingCv.collectAsState()
    val isUploadingPhoto by viewModel.isUploadingPhoto.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }

    var showDeletePhotoDialog by remember { mutableStateOf(false) }
    var showDeleteCvDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // File picker for CV (PDF only)
    val cvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadCv(context, it) }
    }

    // File picker for Photo (Images only)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadProfilePicture(context, it) }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }

    // Show snackbar when update is successful
    LaunchedEffect(profileState) {
        val state = profileState
        if (state is ProfileState.Success && state.updateMessage != null) {
            snackbarHostState.showSnackbar(state.updateMessage)
        }
    }

    // Delete Dialogs
    if (showDeletePhotoDialog) {
        AlertDialog(
            onDismissRequest = { showDeletePhotoDialog = false },
            title = { Text("Hapus Foto Profil") },
            text = { Text("Apakah Anda yakin ingin menghapus foto profil ini?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProfilePicture()
                    showDeletePhotoDialog = false
                }) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePhotoDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showDeleteCvDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteCvDialog = false },
            title = { Text("Hapus CV / Resume") },
            text = { Text("Apakah Anda yakin ingin menghapus CV ini?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCv()
                    showDeleteCvDialog = false
                }) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCvDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundLight,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Profil Pengguna", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight,
                    titleContentColor = TextPrimaryLight
                ),
                actions = {
                    if (profileState is ProfileState.Success && !isEditing) {
                        IconButton(onClick = {
                            val user = (profileState as ProfileState.Success).user
                            editName = user.name
                            editPhone = user.jobSeeker?.phone ?: ""
                            isEditing = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profil",
                                tint = BluePrimary
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (profileState) {
                is ProfileState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BluePrimary
                    )
                }
                is ProfileState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (profileState as ProfileState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(
                            onClick = { viewModel.fetchProfile() },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is ProfileState.Success -> {
                    val user = (profileState as ProfileState.Success).user

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar Area
                        Box(
                            modifier = Modifier.size(110.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8EAF6))
                                    .clickable {
                                        if (!isUploadingPhoto) photoPickerLauncher.launch("image/*")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isUploadingPhoto) {
                                    CircularProgressIndicator(color = BluePrimary, strokeWidth = 3.dp)
                                } else if (user.jobSeeker?.profilePicture != null) {
                                    // Use absolute URL pointing to Laravel storage
                                    val imageUrl = "http://192.168.100.203:8000/storage/${user.jobSeeker.profilePicture}"
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = "Foto Profil",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(60.dp),
                                        tint = BluePrimary
                                    )
                                }
                            }
                            
                            // Edit Icon Badge
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(BluePrimary)
                                    .clickable {
                                        if (!isUploadingPhoto) photoPickerLauncher.launch("image/*")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Foto",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        if (user.jobSeeker?.profilePicture != null && !isEditing) {
                            TextButton(
                                onClick = { showDeletePhotoDialog = true },
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text("Hapus Foto", color = Color.Red, fontSize = 12.sp)
                            }
                        } else {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (!isEditing) {
                            // === VIEW MODE ===
                            Text(
                                text = user.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )

                            Text(
                                text = user.email,
                                fontSize = 16.sp,
                                color = TextSecondaryLight
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                color = Color(0xFFE8EAF6),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "PELAMAR AKTIF",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Info Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "Informasi Pribadi",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextPrimaryLight
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                                    // Nama
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = BluePrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Column {
                                            Text("Nama Lengkap", fontSize = 12.sp, color = TextSecondaryLight)
                                            Text(user.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimaryLight)
                                        }
                                    }

                                    Spacer(Modifier.height(16.dp))

                                    // Email
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Email,
                                            contentDescription = null,
                                            tint = BluePrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Column {
                                            Text("Email", fontSize = 12.sp, color = TextSecondaryLight)
                                            Text(user.email, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimaryLight)
                                        }
                                    }

                                    Spacer(Modifier.height(16.dp))

                                    // Nomor HP
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Phone,
                                            contentDescription = null,
                                            tint = BluePrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Column {
                                            Text("Nomor HP / WhatsApp", fontSize = 12.sp, color = TextSecondaryLight)
                                            Text(
                                                text = user.jobSeeker?.phone ?: "Belum diisi",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (user.jobSeeker?.phone != null) TextPrimaryLight else TextSecondaryLight
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // CV & Resume Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "CV / Resume",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = TextPrimaryLight
                                        )
                                        if (user.jobSeeker?.cvPath != null || user.jobSeeker?.resume != null) {
                                            IconButton(
                                                onClick = { showDeleteCvDialog = true },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Hapus CV",
                                                    tint = Color.Red,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                                    val cvPath = user.jobSeeker?.cvPath ?: user.jobSeeker?.resume

                                    if (cvPath != null) {
                                        // CV sudah diupload
                                        Surface(
                                            color = Color(0xFFE8F5E9),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(44.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(Color(0xFFC8E6C9)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Description,
                                                        contentDescription = null,
                                                        tint = Color(0xFF2E7D32),
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                                Spacer(Modifier.width(12.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "CV Sudah Diupload",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = Color(0xFF2E7D32)
                                                    )
                                                    Text(
                                                        text = cvPath.substringAfterLast("/"),
                                                        fontSize = 12.sp,
                                                        color = Color(0xFF4CAF50)
                                                    )
                                                }
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color(0xFF4CAF50),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Tombol upload ulang
                                        OutlinedButton(
                                            onClick = { cvPickerLauncher.launch("application/pdf") },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            enabled = !isUploadingCv
                                        ) {
                                            if (isUploadingCv) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(18.dp),
                                                    strokeWidth = 2.dp,
                                                    color = BluePrimary
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text("Mengupload...")
                                            } else {
                                                Icon(
                                                    Icons.Default.UploadFile,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text("Upload CV Baru", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    } else {
                                        // CV belum diupload
                                        Surface(
                                            color = Color(0xFFFFF3E0),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = Color(0xFFE65100),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(Modifier.width(12.dp))
                                                Text(
                                                    text = "Anda belum mengupload CV. Upload CV untuk meningkatkan peluang diterima.",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFFE65100)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Tombol upload
                                        Button(
                                            onClick = { cvPickerLauncher.launch("application/pdf") },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                            contentPadding = PaddingValues(14.dp),
                                            enabled = !isUploadingCv
                                        ) {
                                            if (isUploadingCv) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(18.dp),
                                                    strokeWidth = 2.dp,
                                                    color = Color.White
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text("Mengupload...", fontWeight = FontWeight.Bold)
                                            } else {
                                                Icon(
                                                    Icons.Default.UploadFile,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                    text = "Upload CV (PDF)",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Edit Profil Button
                            Button(
                                onClick = {
                                    editName = user.name
                                    editPhone = user.jobSeeker?.phone ?: ""
                                    isEditing = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Edit Profil",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Logout Button
                            Button(
                                onClick = onLogout,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEEBEE)),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = "Logout",
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Keluar Akun",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                        } else {
                            // === EDIT MODE ===
                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "Edit Profil",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF1A237E)
                                    )
                                    Text(
                                        text = "Perbarui informasi profil Anda",
                                        fontSize = 13.sp,
                                        color = TextSecondaryLight
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Nama Lengkap
                                    OutlinedTextField(
                                        value = editName,
                                        onValueChange = { editName = it },
                                        label = { Text("Nama Lengkap") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BluePrimary,
                                            focusedLabelColor = BluePrimary
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Email (disabled)
                                    OutlinedTextField(
                                        value = user.email,
                                        onValueChange = { },
                                        label = { Text("Email") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondaryLight)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        enabled = false,
                                        singleLine = true
                                    )
                                    Text(
                                        text = "Email akun tidak dapat diubah.",
                                        fontSize = 11.sp,
                                        color = TextSecondaryLight,
                                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Nomor HP
                                    OutlinedTextField(
                                        value = editPhone,
                                        onValueChange = { editPhone = it },
                                        label = { Text("Nomor HP / WhatsApp") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Phone, contentDescription = null, tint = BluePrimary)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BluePrimary,
                                            focusedLabelColor = BluePrimary
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Simpan Button
                            Button(
                                onClick = {
                                    viewModel.updateProfile(editName, editPhone)
                                    isEditing = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(16.dp),
                                enabled = editName.isNotBlank()
                            ) {
                                Text(
                                    text = "Simpan Perubahan",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Batal Button
                            OutlinedButton(
                                onClick = { isEditing = false },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text(
                                    text = "Batal",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
