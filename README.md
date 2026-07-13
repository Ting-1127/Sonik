# Sonik

基于 [Lune Music Player](https://github.com/MrDemonc/Lune) 修改的离线音乐播放器。

## 特性

- 🎵 100% 离线播放，无需联网
- 🔒 隐私优先，无广告，无追踪
- 🎨 Material Design 3 + 动态主题
- 🎛️ 10段均衡器 + 低音增强
- 📝 LRC 同步歌词
- 🎶 播放列表管理
- 📊 48频段音频可视化
- 🌙 AMOLED 纯黑模式
- 🌍 支持 8 种语言

## 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高
- JDK 21
- Android SDK API 36
- Gradle 9.4.1

## 构建

```bash
git clone https://github.com/Ting-1127/Sonik.git
cd Sonik
./gradlew assembleDebug
```

APK 输出：`app/build/outputs/apk/debug/Sonik-debug.apk`

## 修改内容

基于 Lune Music Player 修改：
- 包名：`com.demonlab.lune` → `com.ting.sonik`
- 应用名：`Lune` → `Sonik`
- 优化引导页流程（10步→3步）
- 国内 Maven 源配置
- About 页面：添加修改者信息

## 原项目

- [Lune Music Player](https://github.com/MrDemonc/Lune) by MrDemonc

## 许可证

[GPLv3](LICENSE) - 详见 [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.html)
