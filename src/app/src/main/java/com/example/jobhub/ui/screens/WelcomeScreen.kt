package com.example.jobhub.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobhub.R
import com.example.jobhub.ui.theme.BackgroundLight
import com.example.jobhub.ui.theme.BluePrimary
import com.example.jobhub.ui.theme.TextPrimaryLight
import com.example.jobhub.ui.theme.TextSecondaryLight

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Logo
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BluePrimary,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "JOBHUB",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BluePrimary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Hero Image
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFE0E0E0),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.welcome_hero),
                    contentDescription = "Welcome Hero",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Typography
            Text(
                text = "Temukan Pekerjaan Impianmu",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Akses ribuan peluang karir dari perusahaan terkemuka. Langkah awal menuju masa depan yang lebih cerah dimulai di sini.",
                fontSize = 13.sp,
                color = TextSecondaryLight,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Buttons
            Button(
                onClick = onNavigateToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Mulai Sekarang",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = onNavigateToLogin
            ) {
                Text(
                    text = "Sudah punya akun? Masuk",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Footer
            Text(
                text = "DIPERCAYA OLEH",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryLight,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Business, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp).padding(horizontal = 4.dp))
                Icon(Icons.Default.Work, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp).padding(horizontal = 4.dp))
                Icon(Icons.Default.Business, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp).padding(horizontal = 4.dp))
                Icon(Icons.Default.Business, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp).padding(horizontal = 4.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "© 2024 JOBHUB Corporation. Seluruh hak cipta dilindungi.",
                fontSize = 10.sp,
                color = TextSecondaryLight
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
