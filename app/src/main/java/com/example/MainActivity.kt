package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.navigation.ElectraAppNavigation
import com.example.ui.theme.ElectraSenseTheme
import com.example.ui.viewmodel.ElectraViewModel
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {

    private val viewModel: ElectraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        // STEP 2: Enable Edge-to-Edge full bleed
        enableEdgeToEdge()

        // STEP 3: Initialize UI
        setContent {
            ElectraSenseTheme {
                ElectraAppNavigation(viewModel = viewModel)
            }
        }
    }
}
