# Sonik 音乐播放器 Code Wiki

## 目录
1. [项目概览](#项目概览)
2. [技术栈与依赖](#技术栈与依赖)
3. [项目架构](#项目架构)
4. [核心模块详解](#核心模块详解)
5. [数据模型与数据库](#数据模型与数据库)
6. [UI 层架构](#ui-层架构)
7. [音频处理系统](#音频处理系统)
8. [配置与设置](#配置与设置)
9. [构建与运行](#构建与运行)
10. [关键流程说明](#关键流程说明)

---

## 项目概览

### 项目简介
Sonik 是一款基于 [Lune Music Player](https://github.com/MrDemonc/Lune) 修改的**离线音乐播放器**，采用 Kotlin + Jetpack Compose 开发，专注于隐私保护和高质量的本地音乐播放体验。

### 核心特性
- **100% 离线播放**：无需网络连接，所有音乐均来自本地存储
- **隐私优先**：无广告、无用户追踪、无数据收集
- **Material Design 3**：采用最新的 Material You 动态主题设计
- **10段均衡器 + 低音增强**：专业级音频调节
- **LRC 同步歌词**：支持本地 LRC 文件和内嵌歌词
- **播放列表管理**：完整的创建、编辑、导入导出功能
- **48频段音频可视化**：实时音频频谱显示
- **AMOLED 纯黑模式**：为 OLED 屏幕优化的深色主题
- **多语言支持**：支持 8 种语言（中文、英文、德语、西班牙语、法语、波斯语、葡萄牙语、俄语）

### 基本信息
| 属性 | 值 |
|------|-----|
| 应用包名 | `com.ting.sonik` |
| 版本号 | 1.5.1 (versionCode: 9) |
| 最低 SDK | API 24 (Android 7.0) |
| 目标 SDK | API 36 (Android 14+) |
| 编程语言 | Kotlin |
| UI 框架 | Jetpack Compose |
| 许可证 | GPLv3 |

---

## 技术栈与依赖

### 核心框架
| 类别 | 技术/库 | 版本 | 用途 |
|------|---------|------|------|
| 构建系统 | Gradle | 9.4.1 | 项目构建 |
| Android Gradle Plugin | AGP | 9.2.1 | Android 构建插件 |
| Kotlin | Kotlin | 2.2.20 | 开发语言 |
| UI 框架 | Jetpack Compose | 2024.09.00 (BOM) | 声明式 UI |
| Material Design | Material 3 | 1.5.0-alpha15 | 设计系统 |
| 依赖注入 | KSP | 2.3.2 | 编译时代码生成 |
| 数据库 | Room | 2.7.0 | 本地持久化 |

### 主要依赖库
| 库名 | 版本 | 用途 |
|------|------|------|
| `androidx.core:core-ktx` | 1.10.1 | Android KTX 扩展 |
| `androidx.media:media` | 1.7.0 | 媒体会话/浏览器服务 |
| `androidx.lifecycle` | 2.6.1 | 生命周期管理 |
| `androidx.activity:activity-compose` | 1.8.0 | Activity Compose 集成 |
| `io.coil-kt:coil-compose` | 2.5.0 | 图片加载 |
| `com.google.code.gson:gson` | 2.14.0 | JSON 序列化 |
| `net.jthink:jaudiotagger` | 3.0.1 | 音频标签读写 |
| `androidx.appcompat` | 1.6.1 | 兼容性支持 |
| `androidx.documentfile` | 1.0.1 | SAF 文档访问 |

### 模块依赖图
```
Sonik Application
├── ui (用户界面层)
│   ├── activities (Activity 容器)
│   ├── screens (屏幕页面)
│   ├── screens/resume (首页各区块)
│   ├── player (播放器组件)
│   ├── sheets (底部弹窗)
│   ├── search (搜索界面)
│   ├── playlist (播放列表界面)
│   ├── components (通用组件)
│   ├── viewmodels (ViewModel)
│   ├── theme (主题系统)
│   ├── utils (UI 工具)
│   └── data (UI 数据模型)
├── tools (业务逻辑层)
│   ├── playlist (队列管理)
│   ├── MusicService (播放服务)
│   ├── PlaybackManager (播放管理器)
│   ├── MusicProvider (音乐数据提供者)
│   ├── SettingsManager (设置管理器)
│   ├── MetadataManager (元数据管理器)
│   └── ...
├── audio (音频效果层)
│   ├── ReverbEffect (混响效果)
│   ├── Equalizer (均衡器)
│   ├── BassBoost (低音增强)
│   ├── Virtualizer (空间音频)
│   ├── LoudnessEffect (响度效果)
│   ├── DynamicsEffect (动态效果)
│   └── BalanceEffect (平衡效果)
└── data (数据层)
    └── MusicDatabase (Room 数据库)
```

---

## 项目架构

### 架构模式
项目采用 **MVVM (Model-View-ViewModel)** 架构模式，结合单例模式管理全局状态。

```
┌─────────────────────────────────────────────────┐
│                   View (UI)                      │
│  Activities / Composables / Screens              │
└───────────────────┬─────────────────────────────┘
                    │ 状态观察 / 事件回调
┌───────────────────▼─────────────────────────────┐
│               ViewModel                          │
│  MusicViewModel                                  │
└───────────────────┬─────────────────────────────┘
                    │ 数据调用
┌───────────────────▼─────────────────────────────┐
│              Manager / Provider 层                │
│  PlaybackManager / MusicProvider / SettingsManager│
│  MetadataManager / PlaylistBackupManager         │
└───────────────────┬─────────────────────────────┘
                    │ 数据访问
┌───────────────────▼─────────────────────────────┐
│              Data Layer                          │
│  Room Database / MediaStore / SharedPreferences  │
└─────────────────────────────────────────────────┘
```

### 核心单例
项目中多处使用**单例模式**管理全局状态：

| 单例类 | 职责 |
|--------|------|
| `PlaybackManager` | 播放状态管理、播放控制、均衡器设置 |
| `SettingsManager` | 用户设置持久化与读取 |
| `MusicDatabase` | Room 数据库实例 |

### 服务架构
- **前台服务**：`MusicService` 继承自 `MediaBrowserServiceCompat`，提供媒体浏览和播放控制
- **MediaSession**：与系统媒体框架集成，支持锁屏控制、耳机按钮等
- **Widget**：`SonikWidgetProvider` 提供桌面小组件

---

## 核心模块详解

### 1. Application 层

#### SonikApplication
**位置**：[SonikApplication.kt](file:///workspace/app/src/main/java/com/ting/sonik/SonikApplication.kt)

**职责**：
- 应用入口点
- 配置 Coil 图片加载器
- 注册自定义 `AudioThumbnailFetcher` 用于从音频文件提取封面
- 配置磁盘缓存（64MB）

**关键函数**：
- `newImageLoader()`: 创建 Coil ImageLoader 实例，配置自定义 fetcher 和磁盘缓存

---

### 2. 播放服务层

#### MusicService
**位置**：[MusicService.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/MusicService.kt)

**职责**：
- 音乐播放前台服务，继承 `MediaBrowserServiceCompat`
- 管理 `MediaPlayer` 实例（主播放器 + 副播放器用于交叉淡入淡出）
- 音频焦点管理
- 通知栏显示与控制
- 音频效果链管理（均衡器、低音增强、空间音频、混响等）
- 歌词提取
- 桌面小组件更新
- 媒体浏览器服务（支持 Android Auto 等）

**核心成员**：
| 成员 | 类型 | 说明 |
|------|------|------|
| `mediaPlayer` | `MediaPlayer?` | 主播放器 |
| `secondaryPlayer` | `MediaPlayer?` | 副播放器（用于交叉淡入淡出） |
| `mediaSession` | `MediaSessionCompat?` | 媒体会话 |
| `equalizer` | `Equalizer?` | 均衡器效果 |
| `bassBoost` | `BassBoost?` | 低音增强效果 |
| `virtualizer` | `Virtualizer?` | 空间音频效果 |
| `loudnessEffect` | `LoudnessEffect?` | 响度效果 |
| `reverbEffect` | `ReverbEffect?` | 混响效果 |
| `dynamicsEffect` | `DynamicsEffect?` | 动态效果 |

**关键函数**：

| 函数 | 说明 |
|------|------|
| `onCreate()` | 服务创建，初始化 MediaSession、音频设备监听 |
| `playSong(song: Song)` | 播放指定歌曲（直接切换） |
| `crossfadeToSong(song: Song)` | 交叉淡入淡出切换歌曲 |
| `performCrossfade(nextSong, fadeDurationMs)` | 执行交叉淡入淡出动画 |
| `pause()` | 暂停播放 |
| `resume()` | 恢复播放 |
| `seekTo(pos: Int)` | 跳转到指定位置 |
| `restorePlayback(song, positionMs, andPlay)` | 恢复播放状态 |
| `setupAudioFx(sessionId, isSecondary)` | 设置音频效果链 |
| `extractLyrics(song: Song)` | 提取歌曲歌词 |
| `showNotification(song, isPlaying, art)` | 显示播放通知 |
| `updateMetadata(song, art)` | 更新 MediaSession 元数据 |
| `updatePlaybackState()` | 更新播放状态 |

**歌词提取策略**（按优先级）：
1. 同名目录下的 `.lrc` 文件
2. Jaudiotagger 库提取内嵌歌词（FLAC/Vorbis 效果最好）
3. `MediaMetadataRetriever` 原生提取
4. 手动扫描文件头查找 `LYRICS=` / `UNSYNCEDLYRICS=` 标签

**广播动作常量**：
- `ACTION_PLAY` - 播放
- `ACTION_PAUSE` - 暂停
- `ACTION_PREVIOUS` - 上一首
- `ACTION_NEXT` - 下一首
- `ACTION_SHUFFLE` - 切换随机
- `ACTION_FAVORITE` - 切换收藏

---

#### PlaybackManager
**位置**：[PlaybackManager.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/PlaybackManager.kt)

**职责**：
- 全局播放状态管理（单例）
- 播放队列管理
- 播放模式控制（顺序/随机/单曲循环/列表循环）
- 均衡器设置管理
- 播放速度/音调控制
- 播放统计追踪
- 睡眠定时器
- 音频可视化数据管理
- 音频输出设备监测
- 播放状态持久化与恢复

**核心状态**：
| 状态 | 类型 | 说明 |
|------|------|------|
| `currentSong` | `Song?` | 当前播放歌曲 |
| `isPlaying` | `Boolean` | 是否正在播放 |
| `activePlaylist` | `List<Song>` | 当前播放队列 |
| `activePlaylistId` | `Long?` | 当前播放列表 ID |
| `activeCategory` | `String?` | 当前播放分类 |
| `isShuffle` | `Boolean` | 随机播放模式 |
| `repeatMode` | `Int` | 循环模式（0:关 1:单曲 2:列表） |
| `isCrossfade` | `Boolean` | 交叉淡入淡出 |
| `playbackSpeed` | `Float` | 播放速度 |
| `playbackPitch` | `Float` | 播放音调 |

**关键函数**：

| 函数 | 说明 |
|------|------|
| `getInstance(context)` | 获取单例实例 |
| `play(song, playlist, ...)` | 播放指定歌曲及队列 |
| `playNextFromService(isNaturalEnd)` | 播放下一首 |
| `playPreviousFromService()` | 播放上一首（>3秒则从头开始） |
| `pause()` | 暂停 |
| `resume()` | 恢复 |
| `toggleShuffle()` | 切换随机模式 |
| `toggleRepeatMode()` | 切换循环模式 |
| `toggleFavorite(song)` | 切换收藏状态 |
| `savePlaybackState()` | 保存播放状态 |
| `restorePlaybackState(songs)` | 恢复播放状态 |
| `startVisualizer()` | 启动音频可视化 |
| `stopVisualizer()` | 停止音频可视化 |
| `getSortedList(list, option, ascending)` | 获取排序后的列表 |
| `updatePlaybackStats(type, id, ...)` | 更新播放统计 |
| `reorderQueueForSong(song, moveToFront)` | 重新排序队列 |
| `getCurrentQueue()` | 获取当前播放队列 |

**可视化实现**：
- 使用 `Visualizer` API 获取 FFT 数据
- 48 频段显示
- EMA（指数移动平均）平滑处理
- 需要 `RECORD_AUDIO` 权限

---

### 3. 数据提供层

#### MusicProvider
**位置**：[MusicProvider.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/MusicProvider.kt)

**职责**：
- 从 MediaStore 查询音乐文件
- 音乐数据缓存（JSON 文件）
- 应用用户元数据覆盖
- 音乐库刷新（触发 MediaScanner）

**关键函数**：

| 函数 | 说明 |
|------|------|
| `getCachedSongs()` | 从缓存文件读取歌曲列表 |
| `syncSongs()` | 从 MediaStore 同步歌曲列表 |
| `refreshLibrary()` | 刷新媒体库（扫描文件） |
| `updateSongInCache(updatedSong)` | 更新缓存中的单首歌曲 |

**数据同步流程**：
1. 检查存储权限
2. 从 MediaStore 查询所有音乐文件
3. 从 Room 数据库读取用户元数据覆盖
4. 应用覆盖（标题、艺术家、专辑、封面、收藏状态）
5. 计算附加信息（文件夹名、格式、比特率、HiFi 标记）
6. 保存到 JSON 缓存文件

**支持的音频格式**：
MP3, FLAC, WAV, AAC/M4A, OGG, OPUS, WMA, ALAC

---

#### MetadataManager
**位置**：[MetadataManager.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/MetadataManager.kt)

**职责**：
- 管理歌曲元数据的用户自定义覆盖
- 收藏状态管理
- 自定义封面保存
- 元数据恢复

**关键函数**：

| 函数 | 说明 |
|------|------|
| `updateSongMetadata(songId, title, artist, ...)` | 更新歌曲元数据 |
| `updateFavoriteStatus(songId, isFavorite)` | 更新收藏状态 |
| `clearMetadataOverride(songId)` | 清除元数据覆盖（保留收藏） |
| `saveCustomCover(songId, imageUri)` | 保存自定义封面图片 |

---

### 4. 设置管理

#### SettingsManager
**位置**：[SettingsManager.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/SettingsManager.kt)

**职责**：
- 所有用户设置的持久化管理（基于 SharedPreferences）
- 单例模式全局访问
- 设置项的响应式流（部分支持）

**设置分类**：

| 类别 | 主要设置项 |
|------|-----------|
| **界面外观** | 主题模式、AMOLED 纯黑、自定义颜色、封面样式、控件样式 |
| **播放控制** | 随机播放、循环模式、交叉淡入淡出、播放速度、音调 |
| **音频效果** | 均衡器、低音增强、空间音频、混响、响度、动态 |
| **音乐库** | 隐藏文件夹、音乐源、排序方式、HiFi 标记 |
| **功能开关** | 屏幕常亮、手势控制、歌词显示、可视化、震动反馈 |
| **数据统计** | 每日收听时长、播放统计 |
| **备份** | 播放列表备份/恢复 |

**关键设置项**：
- `themeMode`: 主题模式（0:自动 1:浅色 2:深色）
- `isShuffle`: 随机播放
- `repeatMode`: 循环模式
- `isEqEnabled`: 均衡器开关
- `eqBandLevels`: 均衡器各频段值（逗号分隔字符串）
- `crossfadeDurationSeconds`: 交叉淡入淡出时长
- `hiddenFolders`: 隐藏的文件夹集合
- `sortOption`: 排序方式
- `playbackSpeed`: 播放速度

---

### 5. 播放列表与队列

#### QueueManager
**位置**：[QueueManager.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/playlist/QueueManager.kt)

**职责**：
- 播放队列重排工具
- 对象形式，提供静态方法

**关键函数**：

| 函数 | 说明 |
|------|------|
| `moveToFront(playlist, currentSong, targetSong, frontCount)` | 将歌曲移到当前播放歌曲之后 |
| `moveToEnd(playlist, currentSong, targetSong)` | 将歌曲移到队列末尾 |

#### PlaylistBackupManager
**位置**：[PlaylistBackupManager.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/PlaylistBackupManager.kt)

**职责**：
- 播放列表的导出与导入
- JSON 格式备份

**数据结构**：
```kotlin
data class PlaylistExportData(
    val version: Int = 1,
    val playlists: List<PlaylistData>
)

data class PlaylistData(
    val name: String,
    val songs: List<SongMetadata>
)

data class SongMetadata(
    val title: String,
    val artist: String,
    val duration: Long,
    val dateAdded: Long = 0
)
```

**关键函数**：
- `exportPlaylists(outputStream)`: 导出播放列表到 JSON
- `importPlaylists(inputStream)`: 从 JSON 导入播放列表

---

### 6. ViewModel 层

#### MusicViewModel
**位置**：[MusicViewModel.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/viewmodels/MusicViewModel.kt)

**职责**：
- UI 层数据持有者
- 管理歌曲列表状态
- 管理播放列表状态
- 管理播放统计数据
- 处理歌曲删除（含撤销机制）
- 监听 MediaStore 变化（ContentObserver）

**核心状态**：
| 状态 | 类型 | 说明 |
|------|------|------|
| `allSongs` | `List<Song>` | 所有歌曲 |
| `filteredSongs` | `List<Song>` | 过滤后的歌曲（排除视觉删除） |
| `playlists` | `List<Playlist>` | 所有播放列表 |
| `playlistMappings` | `List<PlaylistSong>` | 播放列表-歌曲映射 |
| `topSongStats` | `List<PlaybackStats>` | 热门歌曲统计 |
| `isLoading` | `Boolean` | 是否正在加载 |
| `visuallyDeletedIds` | `List<Long>` | 视觉删除的歌曲 ID |

**关键函数**：

| 函数 | 说明 |
|------|------|
| `loadSongs()` | 加载歌曲列表 |
| `refreshLibrary()` | 刷新音乐库 |
| `updateMetadata(song, title, artist, ...)` | 更新元数据 |
| `toggleFavorite(song)` | 切换收藏 |
| `createPlaylist(name)` | 创建播放列表 |
| `addSongToPlaylist(playlistId, songId)` | 添加歌曲到播放列表 |
| `removeSongFromPlaylist(playlistId, songId)` | 从播放列表移除歌曲 |
| `deletePlaylist(playlist)` | 删除播放列表 |
| `prepareDeleteSong(song)` | 准备删除（视觉删除） |
| `undoDeleteSong(song)` | 撤销删除 |
| `deleteSongPermanently(songId, ...)` | 永久删除歌曲 |

---

## 数据模型与数据库

### 核心数据类

#### Song
**位置**：[Song.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/Song.kt)

歌曲数据模型，包含歌曲的所有元数据信息。

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 歌曲 ID（MediaStore ID） |
| `albumId` | `Long` | 专辑 ID |
| `title` | `String` | 标题 |
| `artist` | `String` | 艺术家 |
| `album` | `String` | 专辑 |
| `duration` | `Long` | 时长（毫秒） |
| `uri` | `Uri` | 内容 URI |
| `path` | `String` | 文件路径 |
| `dateAdded` | `Long` | 添加时间 |
| `albumArtUri` | `Uri?` | 专辑封面 URI |
| `genre` | `String?` | 流派 |
| `folderName` | `String` | 文件夹名称 |
| `isHiFi` | `Boolean` | 是否高音质 |
| `coverUrl` | `String?` | 自定义封面 URL |
| `isFavorite` | `Boolean` | 是否收藏 |
| `lyrics` | `String?` | 歌词 |
| `format` | `String` | 音频格式 |
| `bitrate` | `Int?` | 比特率 |
| `trackNumber` | `Int` | 音轨号 |

#### Album
**位置**：[Album.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/data/Album.kt)

专辑（或艺术家）UI 数据模型。

---

### Room 数据库

#### MusicDatabase
**位置**：[MusicDatabase.kt](file:///workspace/app/src/main/java/com/ting/sonik/data/MusicDatabase.kt)

**数据库信息**：
- 数据库名：`music_database`
- 当前版本：6
- 支持自动迁移（5 → 6）

**实体表**：

| 表名 | 实体类 | 说明 |
|------|--------|------|
| `song_overrides` | `SongOverride` | 用户元数据覆盖 |
| `playlists` | `Playlist` | 播放列表 |
| `playlist_songs` | `PlaylistSong` | 播放列表-歌曲关联 |
| `playback_stats` | `PlaybackStats` | 播放统计 |

#### 实体详解

**SongOverride（歌曲元数据覆盖）**
| 字段 | 类型 | 说明 |
|------|------|------|
| `songId` | `Long` (主键) | 歌曲 ID |
| `title` | `String?` | 自定义标题 |
| `artist` | `String?` | 自定义艺术家 |
| `album` | `String?` | 自定义专辑 |
| `genre` | `String?` | 自定义流派 |
| `coverUri` | `String?` | 自定义封面 URI |
| `isFavorite` | `Boolean` | 收藏状态 |

**Playlist（播放列表）**
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` (自增主键) | 播放列表 ID |
| `name` | `String` | 播放列表名称 |
| `createdAt` | `Long` | 创建时间 |

**PlaylistSong（播放列表歌曲关联）**
| 字段 | 类型 | 说明 |
|------|------|------|
| `playlistId` | `Long` (联合主键) | 播放列表 ID |
| `songId` | `Long` (联合主键) | 歌曲 ID |
| `addedAt` | `Long` | 添加时间 |

**PlaybackStats（播放统计）**
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `String` (主键) | 统计项 ID (如 "SONG_123") |
| `type` | `String` | 类型 (SONG/PLAYLIST/ARTIST) |
| `playCount` | `Long` | 播放次数 |
| `totalTimeMs` | `Long` | 总播放时长 |
| `lastPlayed` | `Long` | 最后播放时间 |

#### DAO 接口

| DAO | 主要方法 |
|-----|----------|
| `SongOverrideDao` | `getAllOverrides()`, `getFavorites()`, `getOverrideForSong()`, `insertOverride()`, `deleteOverride()` |
| `PlaylistDao` | `getAllPlaylists()`, `getPlaylistById()`, `insertPlaylist()`, `deletePlaylist()`, `addSongToPlaylist()`, `removeSongFromPlaylist()`, `getSongIdsForPlaylist()`, `getPlaylistCountForSong()` |
| `PlaybackStatsDao` | `getStatsById()`, `getTopByCountFlow()`, `getTopByTimeFlow()`, `insertStats()` |

---

## UI 层架构

### Activity 列表

| Activity | 位置 | 职责 |
|----------|------|------|
| `Sonik` | [Sonik.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/activities/Sonik.kt) | 主界面（所有功能的入口） |
| `SettingsActivity` | [SettingsActivity.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/activities/SettingsActivity.kt) | 设置界面 |
| `AboutActivity` | [AboutActivity.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/activities/AboutActivity.kt) | 关于页面 |
| `LyricsActivity` | [LyricsActivity.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/activities/LyricsActivity.kt) | 歌词界面 |
| `EqualizerActivity` | [EqualizerActivity.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/activities/EqualizerActivity.kt) | 均衡器界面 |
| `CustomizationActivity` | [CustomizationActivity.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/activities/CustomizationActivity.kt) | 通用自定义 |
| `CoverCustomizationActivity` | [CoverCustomizationActivity.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/activities/CoverCustomizationActivity.kt) | 封面自定义 |
| `ControlsCustomizationActivity` | [ControlsCustomizationActivity.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/activities/ControlsCustomizationActivity.kt) | 控件自定义 |
| `GestureCustomizationActivity` | [GestureCustomizationActivity.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/activities/GestureCustomizationActivity.kt) | 手势自定义 |
| `BlurCustomizationActivity` | [BlurCustomizationActivity.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/activities/BlurCustomizationActivity.kt) | 模糊效果自定义 |
| `PermissionsActivity` | [PermissionsActivity.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/activities/PermissionsActivity.kt) | 权限请求 |
| `MusicSourceActivity` | [MusicSourceActivity.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/activities/MusicSourceActivity.kt) | 音乐源设置 |

### 主界面结构（Sonik）

主界面采用 **Scaffold + 顶部 Tab + 迷你播放器** 的布局结构。

**标签页（Tab）**：
1. **RESUME** - 首页（推荐、最近添加、热门播放列表等）
2. **ALL** - 全部歌曲
3. **FAVORITES** - 收藏（仅当有收藏歌曲时显示）
4. **ALBUMS** - 专辑/艺术家浏览
5. **PLAYLISTS** - 播放列表

**核心组件**：
- `LargeTopAppBar` - 可折叠顶部栏（带 Logo 动画）
- 搜索栏
- 标签切换栏
- 歌曲列表（支持快速滚动）
- 迷你播放器（可展开为全屏播放器）
- 底部弹窗（歌曲操作、排序、播放列表选择等）

### UI 模块划分

| 模块 | 位置 | 说明 |
|------|------|------|
| `screens/` | [screens](file:///workspace/app/src/main/java/com/ting/sonik/ui/screens) | 主要屏幕页面 |
| `screens/resume/` | [screens/resume](file:///workspace/app/src/main/java/com/ting/sonik/ui/screens/resume) | 首页各功能区块 |
| `player/` | [player](file:///workspace/app/src/main/java/com/ting/sonik/ui/player) | 播放器组件 |
| `sheets/` | [sheets](file:///workspace/app/src/main/java/com/ting/sonik/ui/sheets) | 底部弹窗 |
| `search/` | [search](file:///workspace/app/src/main/java/com/ting/sonik/ui/search) | 搜索界面 |
| `playlist/` | [playlist](file:///workspace/app/src/main/java/com/ting/sonik/ui/playlist) | 播放列表界面 |
| `components/` | [components](file:///workspace/app/src/main/java/com/ting/sonik/ui/components) | 通用组件 |
| `theme/` | [theme](file:///workspace/app/src/main/java/com/ting/sonik/ui/theme) | 主题系统 |
| `utils/` | [utils](file:///workspace/app/src/main/java/com/ting/sonik/ui/utils) | UI 工具扩展 |

---

## 音频处理系统

### 音频效果链

播放时，音频数据经过以下效果处理链：

```
MediaPlayer
    ↓
Equalizer (10段均衡器)
    ↓
BassBoost (低音增强)
    ↓
Virtualizer (空间音频/环绕声)
    ↓
LoudnessEffect (响度增强)
    ↓
ReverbEffect (混响效果)
    ↓
DynamicsEffect (动态效果)
    ↓
BalanceEffect (左右声道平衡)
    ↓
Audio Output
```

### 效果器详解

#### Equalizer（均衡器）
- 系统 `android.media.audiofx.Equalizer`
- 10 频段调节
- 支持预设（系统预设 + 用户自定义）
- 自定义预设以 JSON 存储在 SharedPreferences

#### BassBoost（低音增强）
- 系统 `android.media.audiofx.BassBoost`
- 强度可调
- 与均衡器低频段协同工作

#### Virtualizer（空间音频）
- 系统 `android.media.audiofx.Virtualizer`
- 模拟环绕声效果
- 强度 0-1000

#### ReverbEffect（混响）
**位置**：[ReverbEffect.kt](file:///workspace/app/src/main/java/com/ting/sonik/audio/ReverbEffect.kt)

- Android 12+ 使用 `EnvironmentalReverb`
- 旧版本使用 `PresetReverb`
- 6 种预设：Small Room, Medium Room, Large Room, Medium Hall, Large Hall, Plate

#### LoudnessEffect（响度）
**位置**：[LoudnessEffect.kt](file:///workspace/app/src/main/java/com/ting/sonik/audio/LoudnessEffect.kt)

- 响度增强效果

#### DynamicsEffect（动态）
**位置**：[DynamicsEffect.kt](file:///workspace/app/src/main/java/com/ting/sonik/audio/DynamicsEffect.kt)

- 动态范围压缩/扩展

#### BalanceEffect（声道平衡）
**位置**：[BalanceEffect.kt](file:///workspace/app/src/main/java/com/ting/sonik/audio/BalanceEffect.kt)

- 左右声道平衡调节

### 交叉淡入淡出（Crossfade）

**特点**：
- 使用双 `MediaPlayer` 实现
- 默认 12 秒交叉淡入淡出（可自定义）
- 音量使用平方曲线平滑过渡
- 过渡完成后切换主副播放器
- 支持 EQ/BassBoost 在过渡时的音量补偿

---

## 配置与设置

### AndroidManifest 权限

| 权限 | 用途 |
|------|------|
| `READ_EXTERNAL_STORAGE` | 读取存储（API ≤ 32） |
| `READ_MEDIA_AUDIO` | 读取音频（API ≥ 33） |
| `BLUETOOTH_CONNECT` | 蓝牙连接 |
| `POST_NOTIFICATIONS` | 发送通知 |
| `FOREGROUND_SERVICE` | 前台服务 |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | 媒体播放前台服务 |
| `RECORD_AUDIO` | 音频可视化 |
| `VIBRATE` | 震动反馈 |

### 主题系统

**位置**：[Theme.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/theme/Theme.kt)

**主题模式**：
- 自动（跟随系统）
- 浅色模式
- 深色模式

**颜色方案**：
1. **动态颜色**（Android 12+）：从壁纸提取颜色（Material You）
2. **自定义颜色预设**：
   - Sunset Peach（日落桃）
   - Sage Green（鼠尾草绿）
   - Ocean Breeze（海洋蓝）
   - Lavender Mist（薰衣草紫）
   - Warm Amber（暖琥珀）
3. **AMOLED 纯黑模式**：深色模式下背景为纯黑

### 支持的语言

应用支持 8 种语言：
- 中文 (zh)
- 英语 (默认)
- 德语 (de)
- 西班牙语 (es)
- 波斯语 (fa)
- 法语 (fr)
- 葡萄牙语 (pt-rBR)
- 俄语 (ru)

---

## 构建与运行

### 环境要求

| 工具 | 版本要求 |
|------|---------|
| Android Studio | Hedgehog (2023.1.1) 或更高 |
| JDK | 21 |
| Android SDK | API 36 |
| Gradle | 9.4.1 |

### 构建命令

```bash
# 克隆项目
git clone https://github.com/Ting-1127/Sonik.git
cd Sonik

# 构建 Debug 版本
./gradlew assembleDebug

# 构建 Release 版本（需要 keystore.properties）
./gradlew assembleRelease

# 安装到设备
./gradlew installDebug
```

### 签名配置

Release 构建需要在项目根目录创建 `keystore.properties` 文件：

```properties
storeFile=path/to/keystore.jks
storePassword=your_store_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

如果该文件不存在，Release 构建将跳过签名配置。

### 项目结构

```
Sonik/
├── app/                          # 应用模块
│   ├── schemas/                  # Room 数据库 schema 导出
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ting/sonik/
│   │   │   │   ├── audio/        # 音频效果
│   │   │   │   ├── data/         # 数据库层
│   │   │   │   ├── tools/        # 业务逻辑
│   │   │   │   │   └── playlist/ # 队列管理
│   │   │   │   ├── ui/           # UI 层
│   │   │   │   │   ├── activities/
│   │   │   │   │   ├── components/
│   │   │   │   │   ├── data/
│   │   │   │   │   ├── player/
│   │   │   │   │   ├── playlist/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   └── resume/
│   │   │   │   │   ├── search/
│   │   │   │   │   ├── sheets/
│   │   │   │   │   ├── theme/
│   │   │   │   │   ├── utils/
│   │   │   │   │   └── viewmodels/
│   │   │   │   └── SonikApplication.kt
│   │   │   ├── res/              # 资源文件
│   │   │   └── AndroidManifest.xml
│   │   ├── androidTest/          # 仪器测试
│   │   └── test/                 # 单元测试
│   ├── build.gradle.kts          # 应用级构建配置
│   └── proguard-rules.pro        # 混淆规则
├── gradle/
│   ├── libs.versions.toml        # 依赖版本目录
│   └── wrapper/                  # Gradle Wrapper
├── build.gradle.kts              # 项目级构建配置
├── settings.gradle.kts           # 项目设置
└── README.md
```

---

## 关键流程说明

### 1. 应用启动流程

```
Application.onCreate()
    ↓
配置 Coil ImageLoader
    ↓
MainActivity (Sonik) onCreate()
    ↓
检查是否首次启动 → 是：显示引导页
    ↓
检查存储权限 → 否：请求权限
    ↓
MusicViewModel.loadSongs()
    ↓
MusicProvider.getCachedSongs() → 有缓存先显示
    ↓
MusicProvider.syncSongs() → 从 MediaStore 同步
    ↓
PlaybackManager.restorePlaybackState() → 恢复上次播放状态
    ↓
注册 MediaStore ContentObserver
```

### 2. 歌曲播放流程

```
用户点击歌曲
    ↓
PlaybackManager.play(song, playlist)
    ↓
更新 currentSong / activePlaylist / isPlaying 状态
    ↓
启动 MusicService (前台服务)
    ↓
MusicService.playSong(song)
    ↓
创建/重置 MediaPlayer
    ↓
设置数据源，prepareAsync()
    ↓
onPrepared → start()
    ↓
设置音频效果链 (Equalizer, BassBoost, ...)
    ↓
更新 MediaSession 元数据
    ↓
显示播放通知
    ↓
启动交叉淡入淡出监视器
    ↓
提取歌词
    ↓
启动可视化（如启用）
    ↓
记录播放统计
    ↓
保存播放状态
```

### 3. 歌曲切换流程（下一首）

```
用户点击"下一首"或歌曲播放完成
    ↓
PlaybackManager.playNextFromService()
    ↓
检查 repeatMode
    ├─ 单曲循环 → 重新播放当前歌曲
    └─ 顺序/随机 → 计算下一首
        ↓
检查是否启用 Crossfade
    ├─ 是 → MusicService.crossfadeToSong()
    │       ↓
    │   创建副播放器
    │   准备下一首歌
    │   音量淡入淡出动画
    │   切换主副播放器
    │   释放旧播放器
    └─ 否 → MusicService.playSong()
        ↓
更新 PlaybackManager 状态
更新通知
更新可视化
```

### 4. 歌词提取流程

```
播放新歌曲
    ↓
MusicService.extractLyrics()
    ↓
1. 查找同目录 .lrc 文件 → 找到：使用
    ↓ 未找到
2. Jaudiotagger 提取内嵌歌词 → 成功：使用
    ↓ 失败
3. MediaMetadataRetriever 提取 → 成功：使用
    ↓ 失败
4. 手动扫描文件头查找 LYRICS= / UNSYNCEDLYRICS=
    ↓
PlaybackManager.updateLyrics()
```

### 5. 播放状态持久化

**保存时机**：
- 暂停时
- 应用进入后台（onPause）
- 播放进度每 5 秒保存一次
- 切换歌曲时

**保存内容**（`PlaybackStateSaver`）：
- 当前歌曲 ID
- 播放位置
- 队列歌曲 ID 列表
- 播放列表 ID 和名称
- 播放分类
- 随机播放索引
- 是否正在播放
- 队列分段信息

**恢复时机**：
- 应用启动且歌曲列表加载完成后

---

## 附录

### 常量速查

**循环模式**：
- `0` - 关闭
- `1` - 单曲循环
- `2` - 列表循环

**主题模式**：
- `0` - 自动
- `1` - 浅色
- `2` - 深色

**排序选项**：
- `ALPHABETICAL` - 字母顺序
- `ARTIST` - 艺术家
- `DURATION` - 时长
- `DATE_ADDED` - 添加日期
- `TRACK_NUMBER` - 音轨号

**播放分类**：
- `ALL` - 全部歌曲
- `FAVORITES` - 收藏
- `PLAYLISTS` - 播放列表
- `RESUME` - 首页
- `ALBUMS` - 专辑
- 文件夹名 - 特定文件夹

### 相关文件索引

| 类别 | 文件路径 |
|------|---------|
| 应用入口 | [SonikApplication.kt](file:///workspace/app/src/main/java/com/ting/sonik/SonikApplication.kt) |
| 主界面 | [Sonik.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/activities/Sonik.kt) |
| 播放服务 | [MusicService.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/MusicService.kt) |
| 播放管理 | [PlaybackManager.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/PlaybackManager.kt) |
| 音乐数据 | [MusicProvider.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/MusicProvider.kt) |
| 设置管理 | [SettingsManager.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/SettingsManager.kt) |
| 元数据管理 | [MetadataManager.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/MetadataManager.kt) |
| 数据库 | [MusicDatabase.kt](file:///workspace/app/src/main/java/com/ting/sonik/data/MusicDatabase.kt) |
| 歌曲模型 | [Song.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/Song.kt) |
| 主题系统 | [Theme.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/theme/Theme.kt) |
| ViewModel | [MusicViewModel.kt](file:///workspace/app/src/main/java/com/ting/sonik/ui/viewmodels/MusicViewModel.kt) |
| 混响效果 | [ReverbEffect.kt](file:///workspace/app/src/main/java/com/ting/sonik/audio/ReverbEffect.kt) |
| 播放列表备份 | [PlaylistBackupManager.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/PlaylistBackupManager.kt) |
| 队列管理 | [QueueManager.kt](file:///workspace/app/src/main/java/com/ting/sonik/tools/playlist/QueueManager.kt) |
| 应用清单 | [AndroidManifest.xml](file:///workspace/app/src/main/AndroidManifest.xml) |
| 构建配置 | [build.gradle.kts](file:///workspace/app/build.gradle.kts) |
| 依赖版本 | [libs.versions.toml](file:///workspace/gradle/libs.versions.toml) |

---

*文档生成时间：2026-07-14*  
*Sonik 版本：1.5.1*  
*基于 Lune Music Player 修改*
