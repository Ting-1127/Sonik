package com.ting.sonik.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ting.sonik.tools.SettingsManager
import com.ting.sonik.ui.theme.SonikTheme
import androidx.compose.foundation.clickable
import com.ting.sonik.ui.activities.SettingsSection
import com.ting.sonik.ui.activities.SectionPosition
import com.ting.sonik.ui.activities.SettingsPreferenceItem

class MusicSourceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsManager = SettingsManager.getInstance(this)
        enableEdgeToEdge()

        val folderPicker = registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.OpenDocumentTree()
        ) { uri ->
            uri?.let {
                val path = it.path ?: return@let
                val realPath = if (path.startsWith("/tree/")) {
                    path.removePrefix("/tree/")
                } else {
                    path
                }
                val actualPath = if (realPath.startsWith("primary:")) {
                    "/storage/emulated/0/${realPath.removePrefix("primary:")}"
                } else {
                    realPath
                }
                val current = settingsManager.musicSources.toMutableSet()
                current.add(actualPath)
                settingsManager.musicSources = current
            }
        }

        setContent {
            val themeMode = settingsManager.themeMode
            val systemInDarkTheme = isSystemInDarkTheme()
            val targetDarkTheme = when (themeMode) {
                1 -> false
                2 -> true
                else -> systemInDarkTheme
            }

            SonikTheme(
                darkTheme = targetDarkTheme,
                useCustomColors = settingsManager.useCustomColors,
                customColorPalette = settingsManager.customColorPalette,
                useAmoledPitchBlack = settingsManager.useAmoledPitchBlack
            ) {
                MusicSourceScreen(
                    onBack = { finish() },
                    onAddFolder = { folderPicker.launch(null) },
                    settingsManager = settingsManager
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicSourceScreen(
    onBack: () -> Unit,
    onAddFolder: () -> Unit,
    settingsManager: SettingsManager
) {
    var musicSources by remember { mutableStateOf(settingsManager.musicSources) }
    var useCustomSources by remember { mutableStateOf(musicSources.isNotEmpty()) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("音乐源") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 扫描模式
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "扫描模式",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
            }

            item {
                SettingsSection(title = "") {
                    // 指定文件夹扫描开关
                    SettingsPreferenceItem(
                        headlineText = "指定文件夹扫描",
                        supportingText = if (useCustomSources) "开启后只扫描下方添加的文件夹" else "关闭后扫描设备全部音乐",
                        icon = Icons.Default.Folder,
                        position = SectionPosition.SINGLE,
                        trailingContent = {
                            Switch(
                                checked = useCustomSources,
                                onCheckedChange = { enabled ->
                                    useCustomSources = enabled
                                    if (!enabled) {
                                        settingsManager.musicSources = emptySet()
                                        musicSources = emptySet()
                                    }
                                }
                            )
                        }
                    )
                }
            }

            // 指定文件夹列表（开关开启后显示）
            if (useCustomSources) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "指定文件夹",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                }

                if (musicSources.isEmpty()) {
                    item {
                        SettingsSection(title = "") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.FolderOpen,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "未添加任何文件夹",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "点击下方按钮添加音乐文件夹",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(musicSources.toList()) { path ->
                        SettingsSection(title = "") {
                            SettingsPreferenceItem(
                                headlineText = path.substringAfterLast("/"),
                                supportingText = path,
                                icon = Icons.Default.Folder,
                                position = SectionPosition.SINGLE,
                                trailingContent = {
                                    IconButton(
                                        onClick = {
                                            val current = musicSources.toMutableSet()
                                            current.remove(path)
                                            settingsManager.musicSources = current
                                            musicSources = current
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "删除",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                // 添加按钮
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onAddFolder,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("添加文件夹")
                    }
                }
            }

            // 提示
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "说明",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• 扫描全部：扫描设备上所有音乐文件\n• 指定文件夹：只扫描添加的文件夹中的音乐",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
