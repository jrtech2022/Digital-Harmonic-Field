package com.example

import android.os.Bundle
import android.content.Context
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


// Enable Android DataStore Preferences for favorites storage
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites_prefs")
val FAVORITES_KEY = stringSetPreferencesKey("favorite_keys")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

sealed class AppScreen {
    object ToneGrid : AppScreen()
    data class FieldDetail(val keyCipher: String) : AppScreen()
}

@Composable
fun MainAppScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Read favorites list in real-time from DataStore
    val favorites by remember {
        context.dataStore.data.map { preferences ->
            preferences[FAVORITES_KEY] ?: emptySet()
        }
    }.collectAsState(initial = emptySet())

    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.ToneGrid) }

    // Standard Android Back Handler support
    BackHandler(enabled = currentScreen is AppScreen.FieldDetail) {
        currentScreen = AppScreen.ToneGrid
    }

    // Toggle favorite keys inside DataStore Preferences
    fun toggleFavorite(key: String) {
        coroutineScope.launch {
            context.dataStore.edit { preferences ->
                val current = preferences[FAVORITES_KEY] ?: emptySet()
                val updated = if (current.contains(key)) {
                    current - key
                } else {
                    current + key
                }
                preferences[FAVORITES_KEY] = updated
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = FundoPrincipal
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    is AppScreen.ToneGrid -> {
                        ToneGridScreen(
                            favorites = favorites,
                            onKeySelected = { cipher ->
                                currentScreen = AppScreen.FieldDetail(cipher)
                            }
                        )
                    }
                    is AppScreen.FieldDetail -> {
                        FieldDetailScreen(
                            keyCipher = screen.keyCipher,
                            isFavorite = favorites.contains(screen.keyCipher),
                            onToggleFavorite = { toggleFavorite(screen.keyCipher) },
                            onBack = { currentScreen = AppScreen.ToneGrid }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToneGridScreen(
    favorites: Set<String>,
    onKeySelected: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = if (isLandscape) 8.dp else 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (!isLandscape) {
            // Elegant header section with high-contrast text and glowing border container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(
                        width = 1.dp,
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                AcentoAtivo.copy(alpha = 0.4f),
                                AzulClaro.copy(alpha = 0.1f),
                                AcentoAtivo.copy(alpha = 0.4f)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = FundoSecundario.copy(alpha = 0.85f)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Glow tag
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AcentoAtivo.copy(alpha = 0.12f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = "❖ HARMONIA PREMIUM ❖",
                            color = AcentoAtivo,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.5.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    // Custom programmatically drawn logo with overlapping fretboard pattern & brand typography
                    DigitalHarmonicFieldLogo(
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Selecione o tom desejado para visualizar todos os acordes de uma só vez.",
                        color = TextoSecundario,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }

        // Favorites Row Section (DataStore persisted)
        FavoritesRow(
            favorites = favorites,
            onKeySelected = onKeySelected
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Row 1: Natural Major Keys Info (C, D, E, F, G, A, B)
        val row1 = listOf(
            "C" to "Dó",
            "D" to "Ré",
            "E" to "Mi",
            "F" to "Fá",
            "G" to "Sol",
            "A" to "Lá",
            "B" to "Si"
        )

        // Row 2: Natural Minor Keys Info (Cm, Dm, Em, Fm, Gm, Am, Bm)
        val row2 = listOf(
            "Cm" to "Dó m",
            "Dm" to "Ré m",
            "Em" to "Mi m",
            "Fm" to "Fá m",
            "Gm" to "Sol m",
            "Am" to "Lá m",
            "Bm" to "Si m"
        )

        // Row 3: Sharp & Minor Sharp Keys Info
        val row3Major = listOf(
            "C#" to "Dó#",
            "D#" to "Ré#",
            "F#" to "Fá#",
            "G#" to "Sol#",
            "A#" to "Lá#"
        )
        val row3Minor = listOf(
            "C#m" to "Dó# m",
            "D#m" to "Ré# m",
            "F#m" to "Fá# m",
            "G#m" to "Sol# m",
            "A#m" to "Lá# m"
        )

        // Grid Lines Arrangement
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1 Container (Natural Majors)
            CategoryRowContainer(title = "NOTAS NATURAIS MAIORES") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row1.forEach { (cipher, name) ->
                        Box(modifier = Modifier.weight(1f)) {
                            KeyGridButton(
                                cipher = cipher,
                                namePt = name,
                                isFavorite = favorites.contains(cipher),
                                onClick = { onKeySelected(cipher) }
                            )
                        }
                    }
                }
            }

            // Row 2 Container (Natural Minors)
            CategoryRowContainer(title = "VARIAÇÕES MENORES NATURAIS") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row2.forEach { (cipher, name) ->
                        Box(modifier = Modifier.weight(1f)) {
                            KeyGridButton(
                                cipher = cipher,
                                namePt = name,
                                isFavorite = favorites.contains(cipher),
                                onClick = { onKeySelected(cipher) }
                            )
                        }
                    }
                }
            }

            // Row 3 Container (Sharps & Minor Sharps) - Recorred into two spacious horizontal tracks to prevent name wrapping
            CategoryRowContainer(title = "SUSTENIDOS E MENORES COM SUSTENIDO") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Row 3A: Major Sharps (5 columns - double wide)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        row3Major.forEach { (cipher, name) ->
                            Box(modifier = Modifier.weight(1f)) {
                                KeyGridButton(
                                    cipher = cipher,
                                    namePt = name,
                                    isFavorite = favorites.contains(cipher),
                                    onClick = { onKeySelected(cipher) }
                                )
                            }
                        }
                    }

                    // Row 3B: Minor Sharps (5 columns - double wide)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        row3Minor.forEach { (cipher, name) ->
                            Box(modifier = Modifier.weight(1f)) {
                                KeyGridButton(
                                    cipher = cipher,
                                    namePt = name,
                                    isFavorite = favorites.contains(cipher),
                                    onClick = { onKeySelected(cipher) }
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
fun FavoritesRow(
    favorites: Set<String>,
    onKeySelected: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLandscape) 4.dp else 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "SUAS TONALIDADES FAVORITAS",
                color = Color(0xFFFBBF24),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(FundoSecundario)
                    .border(1.dp, FundoTerciario, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum tom favoritado ainda. Toque em um tom na grade abaixo e marque a estrela de favorito para salvá-lo aqui.",
                    color = TextoDiscreto,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                favorites.sorted().forEach { cipher ->
                    val namePt = when (cipher) {
                        "C" -> "Dó"; "Cm" -> "Dó m"
                        "D" -> "Ré"; "Dm" -> "Ré m"
                        "E" -> "Mi"; "Em" -> "Mi m"
                        "F" -> "Fá"; "Fm" -> "Fá m"
                        "G" -> "Sol"; "Gm" -> "Sol m"
                        "A" -> "Lá"; "Am" -> "Lá m"
                        "B" -> "Si"; "Bm" -> "Si m"
                        "C#" -> "Dó#"; "C#m" -> "Dó# m"
                        "D#" -> "Ré#"; "D#m" -> "Ré# m"
                        "F#" -> "Fá#"; "F#m" -> "Fá# m"
                        "G#" -> "Sol#"; "G#m" -> "Sol# m"
                        "A#" -> "Lá#"; "A#m" -> "Lá# m"
                        else -> cipher
                    }

                    Column(
                        modifier = Modifier
                            .width(80.dp)
                            .height(if (isLandscape) 44.dp else 50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(FundoSecundario)
                            .border(1.2.dp, Color(0xFFFBBF24), RoundedCornerShape(8.dp))
                            .clickable { onKeySelected(cipher) }
                            .testTag("fav_key_$cipher"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = cipher,
                            color = AzulClaro,
                            fontSize = if (isLandscape) 16.sp else 18.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = namePt,
                            color = TextoSecundario,
                            fontSize = if (isLandscape) 9.sp else 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryRowContainer(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            color = TextoDiscreto,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
        )
        content()
    }
}

@Composable
fun KeyGridButton(
    cipher: String,
    namePt: String,
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isLandscape) 46.dp else 52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(FundoSecundario)
            .border(
                1.dp,
                if (isFavorite) Color(0xFFFBBF24) else FundoTerciario,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .testTag("key_btn_$cipher"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = cipher,
                color = AzulClaro,
                fontSize = if (isLandscape) 18.sp else 20.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = namePt,
                color = TextoSecundario,
                fontSize = if (isLandscape) 9.sp else 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (isFavorite) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
            )
        }
    }
}

@Composable
fun FieldDetailScreen(
    keyCipher: String,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    // CRITICAL FUNCTIONALITY PREVENT SCREEN OFF: Keep screen on during music charts viewing
    val currentView = LocalView.current
    DisposableEffect(key1 = Unit) {
        currentView.keepScreenOn = true
        onDispose {
            currentView.keepScreenOn = false
        }
    }

    // Transpose state in +/- semitones
    var transposeOffset by remember { mutableStateOf(0) }
    var selectedChordIndex by remember { mutableStateOf(0) }

    val field = remember(keyCipher) { HarmonicDatabase.getField(keyCipher) }

    if (field == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Tonalidade não encontrada.", color = TextoPrincipal)
        }
        return
    }

    // Calculate transposed chord properties and names in real time
    val transposedKeyNamePt = remember(field, transposeOffset) {
        transposePtChord(field.keyNamePt, transposeOffset)
    }

    val transposedScaleNotes = remember(field, transposeOffset) {
        field.scaleNotes.split(" — ")
            .map { transposePtNote(it, transposeOffset) }
            .joinToString(" — ")
    }

    val transposedChords = remember(field, transposeOffset) {
        field.chords.map { chord ->
            chord.copy(
                cipher = transposeCipher(chord.cipher, transposeOffset),
                portugueseName = transposePtChord(chord.portugueseName, transposeOffset)
            )
        }
    }

    val selectedChord = transposedChords.getOrNull(selectedChordIndex) ?: transposedChords[0]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .then(
                if (isLandscape) Modifier.verticalScroll(rememberScrollState())
                else Modifier
            ),
        verticalArrangement = if (isLandscape) Arrangement.spacedBy(8.dp) else Arrangement.SpaceBetween
    ) {
        // TOP HEADER: Back Button, Tone Title and Favorite Star icon Toggle button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLandscape) 8.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Discrete elegant back button (maximum 2 touches!)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(FundoSecundario)
                    .border(1.dp, FundoTerciario, RoundedCornerShape(8.dp))
                    .clickable { onBack() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("back_button"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = AzulClaro,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Voltar",
                    color = TextoPrincipal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Tone Title
            Column {
                Text(
                    text = "CAMPO HARMÔNICO",
                    color = AcentoAtivo,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = transposedKeyNamePt,
                    color = TextoPrincipal,
                    fontSize = if (isLandscape) 18.sp else 22.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Favorite Toggle Icon Button
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.testTag("favorite_toggle")
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Favoritar Tom",
                    tint = if (isFavorite) Color(0xFFFBBF24) else FundoTerciario,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // TRANSPOR (Transpose Controls +/- Semitones Bar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLandscape) 6.dp else 10.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(FundoSecundario)
                .border(1.dp, FundoTerciario, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "TRANSPOR: ",
                    color = TextoDiscreto,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (transposeOffset == 0) "Tom Original" else if (transposeOffset > 0) "+$transposeOffset semitons" else "$transposeOffset semitons",
                    color = AcentoAtivo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Diminuir Tom (-1)
                Button(
                    onClick = { transposeOffset = if (transposeOffset <= -12) -12 else transposeOffset - 1 },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FundoTerciario),
                    modifier = Modifier.height(26.dp)
                ) {
                    Text("- 1", color = TextoPrincipal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Reset Shift (Zerar Transposição)
                if (transposeOffset != 0) {
                    Button(
                        onClick = { transposeOffset = 0 },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FundoTerciario),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("Zerar", color = AcentoAtivo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Aumentar Tom (+1)
                Button(
                    onClick = { transposeOffset = if (transposeOffset >= 12) 12 else transposeOffset + 1 },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FundoTerciario),
                    modifier = Modifier.height(26.dp)
                ) {
                    Text("+ 1", color = TextoPrincipal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // CENTER CHORD MATRIX & NOTE FORMULATION
        if (isLandscape) {
            // Landscape layout: Row of chords (fixed height) + formulation notes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                transposedChords.forEachIndexed { index, chord ->
                    Box(modifier = Modifier.weight(1f)) {
                        ChordCard(
                            chord = chord,
                            isLandscape = true,
                            isSelected = selectedChordIndex == index,
                            onClick = { selectedChordIndex = index }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            SelectedChordNotesView(
                chord = selectedChord,
                isLandscape = true
            )
        } else {
            // Portrait layout: Entire main listing area scrollable including Cards & Detail Box!
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                transposedChords.forEachIndexed { index, chord ->
                    ChordCard(
                        chord = chord,
                        isLandscape = false,
                        isSelected = selectedChordIndex == index,
                        onClick = { selectedChordIndex = index }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                SelectedChordNotesView(
                    chord = selectedChord,
                    isLandscape = false
                )
            }
        }

        // BOTTOM FOOTER: Scale Notes
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider(color = FundoTerciario, thickness = 1.dp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NOTAS DA ESCALA: ",
                    color = TextoDiscreto,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = transposedScaleNotes,
                    color = AcentoAtivo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun getFeelColor(feel: String): Color {
    val f = feel.trim().lowercase()
    return when {
        f.contains("repouso absoluto") || f.contains("chegada") -> Color(0xFF10B981) // Emerald Green - Calming arrival
        f.contains("afastamento") || f.contains("início do movimento") -> Color(0xFF3B82F6) // Sky Blue - Fresh start
        f.contains("transição suave") || f.contains("repouso flutuante") -> Color(0xFF2DD4BF) // Luminous Teal - Floating suspend
        f.contains("abertura") || f.contains("expansão") -> Color(0xFFF59E0B) // Luminous Amber - Warm expansion
        f.contains("puxada") || f.contains("tensão máxima") -> Color(0xFFEF4444) // Intense Red - Max Tension
        f.contains("falsa resolução") || f.contains("desvio emocional") -> Color(0xFF8B5CF6) // Royal Purple - Mystery detour
        f.contains("instabilidade pura") || f.contains("suspense") -> Color(0xFFD946EF) // Fuchsia - Instability and suspense
        else -> Color(0xFF94A3B8) // Neutral Slate
    }
}

/**
 * Calculates formative notes based on a chord cipher in Portuguese notation context.
 */
fun getChordFormulaNotes(cipher: String): List<Pair<String, String>> {
    var rootCipher = ""
    var isMinor = false
    var isDiminished = false

    // Safely extract root cipher
    if (cipher.startsWith("C#") || cipher.startsWith("D#") || cipher.startsWith("F#") || cipher.startsWith("G#") || cipher.startsWith("A#")) {
        rootCipher = cipher.substring(0, 2)
    } else if (cipher.startsWith("Db") || cipher.startsWith("Eb") || cipher.startsWith("Gb") || cipher.startsWith("Ab") || cipher.startsWith("Bb")) {
        rootCipher = cipher.substring(0, 2)
    } else if (cipher.isNotEmpty()) {
        rootCipher = cipher.substring(0, 1)
    }

    val rest = cipher.substring(rootCipher.length)
    if (rest.contains("m") && !rest.contains("dim")) {
        isMinor = true
    }
    if (rest.contains("°") || rest.contains("dim") || rest.contains("o")) {
        isDiminished = true
    }

    // Map cipher root to chromatic index (0-11)
    val rootsMap = mapOf(
        "C" to 0, "C#" to 1, "Db" to 1, "D" to 2, "D#" to 3, "Eb" to 3,
        "E" to 4, "F" to 5, "F#" to 6, "Gb" to 6, "G" to 7, "G#" to 8,
        "Ab" to 8, "A" to 9, "A#" to 10, "Bb" to 10, "B" to 11
    )

    val rootIndex = rootsMap[rootCipher] ?: 0

    // Choose flat or sharp representation based on chord context
    val useFlats = cipher.contains("b") || cipher.contains("F") || cipher.contains("Bb")
    val scale = if (useFlats) {
        listOf("Dó", "Réb", "Ré", "Mib", "Mi", "Fá", "Solb", "Sol", "Láb", "Lá", "Sib", "Si")
    } else {
        listOf("Dó", "Dó#", "Ré", "Ré#", "Mi", "Fá", "Fá#", "Sol", "Sol#", "Lá", "Lá#", "Si")
    }

    val rootNote = scale[rootIndex]
    
    val (thirdNote, thirdLabel) = if (isDiminished) {
        scale[(rootIndex + 3) % 12] to "Terça Menor: Define o tom menor/diminuto"
    } else if (isMinor) {
        scale[(rootIndex + 3) % 12] to "Terça Menor: Define que o acorde é menor"
    } else {
        scale[(rootIndex + 4) % 12] to "Terça Maior: Define que o acorde é maior"
    }

    val (fifthNote, fifthLabel) = if (isDiminished) {
        scale[(rootIndex + 6) % 12] to "Quinta Diminuta: Dá a característica tensa do acorde diminuto"
    } else {
        scale[(rootIndex + 7) % 12] to "Quinta Justa: Dá a estabilidade e corpo ao som"
    }

    return listOf(
        rootNote to "Fundamental/Tônica: Dá o nome e a base ao acorde",
        thirdNote to thirdLabel,
        fifthNote to fifthLabel
    )
}

@Composable
fun SelectedChordNotesView(
    chord: ChordInfo,
    isLandscape: Boolean
) {
    val formulaNotes = remember(chord.cipher) {
        getChordFormulaNotes(chord.cipher)
    }
    val feelColor = getFeelColor(chord.functionFeel)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(FundoSecundario.copy(alpha = 0.9f))
            .border(
                width = 1.5.dp,
                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                    colors = listOf(feelColor, feelColor.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = feelColor.copy(alpha = 0.15f),
                modifier = Modifier
                    .border(1.dp, feelColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = chord.degree,
                    color = TextoPrincipal,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Formação do Acorde: ",
                    color = TextoSecundario,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${chord.cipher} (${chord.portugueseName})",
                    color = feelColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                formulaNotes.forEach { (note, desc) ->
                    val parts = desc.split(":")
                    val interval = parts[0]
                    val detail = if (parts.size > 1) parts[1].trim() else ""

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(FundoPrincipal)
                            .border(1.dp, feelColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = note,
                            color = feelColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = interval,
                            color = TextoPrincipal,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        if (detail.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                text = detail,
                                color = TextoSecundario,
                                fontSize = 9.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                formulaNotes.forEach { (note, desc) ->
                    val parts = desc.split(":")
                    val interval = parts[0]
                    val detail = if (parts.size > 1) parts[1].trim() else ""

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(FundoPrincipal)
                            .border(1.dp, feelColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(feelColor.copy(alpha = 0.15f))
                                .border(1.2.dp, feelColor, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = note,
                                color = feelColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$note ($interval)",
                                color = TextoPrincipal,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (detail.isNotEmpty()) {
                                Text(
                                    text = detail,
                                    color = TextoSecundario,
                                    fontSize = 11.sp
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
fun ChordCard(
    chord: ChordInfo,
    isLandscape: Boolean,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val feelColor = getFeelColor(chord.functionFeel)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isLandscape) Modifier.fillMaxHeight() else Modifier.height(86.dp)
            )
            .border(
                if (isSelected) 2.5.dp else 1.2.dp,
                if (isSelected) feelColor else feelColor.copy(alpha = 0.42f),
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .testTag("chord_card_${chord.cipher}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) FundoSecundario else FundoSecundario.copy(alpha = 0.75f)
        )
    ) {
        if (isLandscape) {
            // Landscape layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Info
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = chord.degree,
                        color = TextoDiscreto,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = chord.functionName,
                        color = feelColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Cipher (Enlarged)
                Text(
                    text = chord.cipher,
                    color = feelColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Portuguese label
                Text(
                    text = chord.portugueseName,
                    color = TextoSecundario,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            // Portrait Layout: Rows with enlarged readability sizes
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left text fields
                Column(modifier = Modifier.weight(1.6f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = feelColor.copy(alpha = 0.15f),
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .border(1.dp, feelColor, RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                text = chord.degree,
                                color = TextoPrincipal,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = chord.functionName,
                            color = feelColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = chord.functionFeel,
                        color = feelColor.copy(alpha = 0.82f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Center Cipher (Enlarged dramatically as requested!)
                Text(
                    text = chord.cipher,
                    color = feelColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1.2f),
                    textAlign = TextAlign.Center
                )

                // Right Portuguese name tag
                Text(
                    text = chord.portugueseName,
                    color = TextoSecundario,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1.4f),
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TextColorOnTag(degree: String): Color {
    return if (degree.any { it.isUpperCase() }) TextoPrincipal else AzulClaro
}

// ==========================================
// MUSICAL CHROME TRANSPOSITION CONTROLLERS
// ==========================================

private fun transposeCipher(cipher: String, semitones: Int): String {
    if (semitones == 0) return cipher
    val roots = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val root: String
    val suffix: String

    if (cipher.length >= 2 && (cipher[1] == '#' || cipher[1] == 'b')) {
        root = cipher.substring(0, 2)
        suffix = cipher.substring(2)
    } else if (cipher.isNotEmpty()) {
        root = cipher.substring(0, 1)
        suffix = cipher.substring(1)
    } else {
        return cipher
    }

    val standardizedRoot = when (root) {
        "Db" -> "C#"
        "Eb" -> "D#"
        "Gb" -> "F#"
        "Ab" -> "G#"
        "Bb" -> "A#"
        else -> root
    }

    val index = roots.indexOf(standardizedRoot)
    if (index == -1) return cipher

    val newIndex = (index + semitones).let {
        val rem = it % 12
        if (rem < 0) rem + 12 else rem
    }

    return roots[newIndex] + suffix
}

private val ptNotesInput = mapOf(
    "Dó" to 0, "Dó#" to 1, "Do" to 0, "Do#" to 1,
    "Ré" to 2, "Ré#" to 3, "Re" to 2, "Re#" to 3,
    "Mi" to 4, "Mí" to 4, "Mi#" to 5, "Mí#" to 5,
    "Fá" to 5, "Fá#" to 6, "Fa" to 5, "Fa#" to 6,
    "Sol" to 7, "Sol#" to 8,
    "Lá" to 9, "Lá#" to 10, "La" to 9, "La#" to 10,
    "Si" to 11, "Si#" to 0, "Sib" to 10
)

private val ptNotesOutput = listOf("Dó", "Dó#", "Ré", "Ré#", "Mi", "Fá", "Fá#", "Sol", "Sol#", "Lá", "Lá#", "Si")

private fun transposePtNote(note: String, semitones: Int): String {
    val cleanNote = note.trim()
    val index = ptNotesInput[cleanNote] ?: return note
    val newIndex = (index + semitones).let {
        val rem = it % 12
        if (rem < 0) rem + 12 else rem
    }
    return ptNotesOutput[newIndex]
}

private fun transposePtChord(name: String, semitones: Int): String {
    val parts = name.split(" ", limit = 2)
    if (parts.isEmpty()) return name
    val transposedNote = transposePtNote(parts[0], semitones)
    return if (parts.size > 1) "$transposedNote ${parts[1]}" else transposedNote
}

@Composable
fun DigitalHarmonicFieldLogo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Programmatic vector fretboard of the brand logo
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .size(width = 150.dp, height = 124.dp)
                .padding(bottom = 8.dp)
        ) {
            val width = size.width
            val height = size.height

            val rightStartX = width * 0.48f
            val rightEndX = width * 0.88f
            val rightWidth = rightEndX - rightStartX
            val numRightStrings = 4
            val numFrets = 6
            
            val startY = height * 0.08f
            val endY = height * 0.88f
            val fretHeight = (endY - startY) / (numFrets - 1)
            
            // Draw right background silver/dark frets
            for (i in 0 until numFrets) {
                val y = startY + i * fretHeight
                drawLine(
                    color = FundoTerciario.copy(alpha = 0.6f),
                    start = androidx.compose.ui.geometry.Offset(rightStartX, y),
                    end = androidx.compose.ui.geometry.Offset(rightEndX, y),
                    strokeWidth = 2.dp.toPx()
                )
                if (i == 0) {
                    drawLine(
                        color = TextoSecundario,
                        start = androidx.compose.ui.geometry.Offset(rightStartX - 4.dp.toPx(), y),
                        end = androidx.compose.ui.geometry.Offset(rightEndX + 4.dp.toPx(), y),
                        strokeWidth = 4.dp.toPx()
                    )
                }
            }
            
            // Draw right vertical strings (silver)
            val stringSpacing = rightWidth / (numRightStrings - 1)
            for (i in 0 until numRightStrings) {
                val x = rightStartX + i * stringSpacing
                drawLine(
                    color = TextoSecundario.copy(alpha = 0.8f),
                    start = androidx.compose.ui.geometry.Offset(x, startY),
                    end = androidx.compose.ui.geometry.Offset(x, endY),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Draw left blue part (frets continuing left)
            val leftStartX = width * 0.12f
            val leftEndX = rightStartX
            for (i in 0 until numFrets) {
                val y = startY + i * fretHeight
                drawLine(
                    color = AzulPrimario.copy(alpha = 0.7f),
                    start = androidx.compose.ui.geometry.Offset(leftStartX, y),
                    end = androidx.compose.ui.geometry.Offset(leftEndX, y),
                    strokeWidth = 2.dp.toPx()
                )
                if (i == 0) {
                    drawLine(
                        color = AcentoAtivo.copy(alpha = 0.8f),
                        start = androidx.compose.ui.geometry.Offset(leftStartX, y),
                        end = androidx.compose.ui.geometry.Offset(leftEndX, y),
                        strokeWidth = 3.dp.toPx()
                    )
                }
            }

            // Left vertical strings
            val leftString1X = leftStartX + (leftEndX - leftStartX) * 0.35f
            val leftString2X = leftStartX + (leftEndX - leftStartX) * 0.70f
            
            // Left string 1
            drawLine(
                color = AzulPrimario,
                start = androidx.compose.ui.geometry.Offset(leftString1X, startY + fretHeight),
                end = androidx.compose.ui.geometry.Offset(leftString1X, startY + 4 * fretHeight),
                strokeWidth = 2.dp.toPx()
            )
            // Left string 2
            drawLine(
                color = AcentoAtivo,
                start = androidx.compose.ui.geometry.Offset(leftString2X, startY + 2 * fretHeight),
                end = androidx.compose.ui.geometry.Offset(leftString2X, startY + 5 * fretHeight),
                strokeWidth = 2.dp.toPx()
            )

            // Draw circular chord nodes (silver)
            drawCircle(
                color = TextoPrincipal,
                radius = 6.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(rightStartX + 1 * stringSpacing, startY + 1 * fretHeight)
            )
            drawCircle(
                color = FundoPrincipal,
                radius = 2.5f.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(rightStartX + 1 * stringSpacing, startY + 1 * fretHeight)
            )
            
            drawCircle(
                color = TextoSecundario,
                radius = 6.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(rightStartX + 2 * stringSpacing, startY + 2 * fretHeight)
            )
            drawCircle(
                color = FundoPrincipal,
                radius = 2.5f.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(rightStartX + 2 * stringSpacing, startY + 2 * fretHeight)
            )
            
            drawCircle(
                color = TextoSecundario,
                radius = 5.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(rightStartX + 0 * stringSpacing, startY + 0 * fretHeight)
            )
            
            drawCircle(
                color = TextoSecundario,
                radius = 5.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(rightStartX + 3 * stringSpacing, startY + 4 * fretHeight)
            )

            // Draw circular chord nodes (blue/cyan)
            drawCircle(
                color = AzulClaro,
                radius = 7.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(leftString1X, startY + 4 * fretHeight)
            )
            drawCircle(
                color = FundoPrincipal,
                radius = 2.5f.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(leftString1X, startY + 4 * fretHeight)
            )
            
            drawCircle(
                color = AcentoAtivo,
                radius = 7.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(leftString2X, startY + 2 * fretHeight)
            )
            drawCircle(
                color = FundoPrincipal,
                radius = 2.5f.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(leftString2X, startY + 2 * fretHeight)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Line 1: DIGITAL HARMONIC FIELD
        Text(
            text = "DIGITAL HARMONIC FIELD",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Line 2: APP FOR MUSICIANS
        Text(
            text = "APP FOR MUSICIANS",
            color = TextoSecundario,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Line 3: JR_TECH_OFC
        Text(
            text = "JR_TECH_OFC",
            color = AcentoAtivo,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center
        )
    }
}



