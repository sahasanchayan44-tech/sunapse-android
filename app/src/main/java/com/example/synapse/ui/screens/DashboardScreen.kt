package com.example.synapse.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.ui.components.NeumorphicCard
import com.example.synapse.ui.theme.NeuTextPrimary
import com.example.synapse.ui.theme.NeuTextSecondary

@Composable
fun DashboardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Status Bar Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Jio 4G LTE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeuTextPrimary)
                Text("09:44 AM", fontSize = 10.sp, color = NeuTextSecondary)
            }
            Text("80%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeuTextPrimary)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile & Welcome Section
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("August, 2020", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeuTextPrimary)
                    
                    // Profile Image with Neumorphic Circle
                    NeumorphicCard(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        elevation = 4.dp
                    ) {
                        Box(modifier = Modifier.padding(4.dp)) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                color = Color.LightGray
                            ) {
                                // Placeholder for profile image
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                    
                    Text("09:44AM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeuTextPrimary)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Sunday, 23", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeuTextPrimary)
                Text("manavik2@gmail.com", fontSize = 12.sp, color = NeuTextSecondary)
                
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Welcome Back", 
                    fontSize = 32.sp, 
                    fontWeight = FontWeight.Black, 
                    color = NeuTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Grid Layout for Music, Weather, etc.
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left Column
            Column(modifier = Modifier.weight(1f)) {
                // Music Card
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            color = Color.LightGray
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.padding(24.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Music", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeuTextPrimary)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Appointments Card
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Appointments", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeuTextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Muharram/Ashura", fontSize = 10.sp, color = NeuTextSecondary)
                        Text("• Onam", fontSize = 10.sp, color = NeuTextSecondary)
                        Text("• Onam", fontSize = 10.sp, color = NeuTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Headlines Card
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        "Headlines", 
                        modifier = Modifier.padding(20.dp),
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.Black, 
                        color = NeuTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Right Column
            Column(modifier = Modifier.weight(1f)) {
                // Now Playing Info
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Humsafar (From \"Badrinath Ki Dulhania\")", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeuTextPrimary)
                        Text("Akhil Sachdeva, Mansheel Gujral", fontSize = 8.sp, color = NeuTextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Best Of Akhil Sachde...", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeuTextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { 0.4f },
                            modifier = Modifier.fillMaxWidth().height(2.dp),
                            color = NeuTextPrimary,
                            trackColor = Color.LightGray.copy(alpha = 0.3f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Media Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) { Icon(Icons.Default.SkipPrevious, contentDescription = null) }
                    NeumorphicCard(shape = CircleShape, elevation = 4.dp) {
                        IconButton(onClick = {}) { Icon(Icons.Default.PlayArrow, contentDescription = null) }
                    }
                    IconButton(onClick = {}) { Icon(Icons.Default.SkipNext, contentDescription = null) }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Weather Card
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Cloud, contentDescription = null, modifier = Modifier.size(48.dp), tint = NeuTextPrimary)
                        Text("scattered clouds", fontSize = 10.sp, color = NeuTextSecondary)
                        Text("Baldeo Bagh", fontSize = 10.sp, color = NeuTextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("29°C", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NeuTextPrimary)
                        Text("H: 31°C L: 24°C", fontSize = 10.sp, color = NeuTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Social Card
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(modifier = Modifier.padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                        Text("Social", fontSize = 24.sp, fontWeight = FontWeight.Black, color = NeuTextPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom Battery/System Card
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                "Battery level is 80%. Last unplugged 19 minutes ago. Expected Battery Time 01:24PM",
                modifier = Modifier.padding(16.dp),
                fontSize = 10.sp,
                color = NeuTextSecondary,
                lineHeight = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom nav
    }
}
