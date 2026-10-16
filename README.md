# AE2 Light UI Compat · AE2 浅色界面适配

用于 **Minecraft 1.20.1 / Forge 47.x / AE2 15.x** 的客户端按钮适配模组。

为 AE2 控件体系注册的通用按钮提供独立贴图和文字颜色，例如机器优先级的加减按钮。原版菜单和其他模组的按钮保持原样；关闭配套资源包后自动恢复原来的按钮样式。服务端无需安装。

## 构建与开发

这是标准 ForgeGradle 工程，不需要 Python。安装 JDK 17 并设置 `JAVA_HOME`，然后执行：

```powershell
.\gradlew.bat build
```

Linux/macOS 使用 `./gradlew build`。首次构建需要联网下载 Gradle、Forge、Minecraft 和 AE2 依赖。工程也包含官方 MDK 的 Java toolchain 下载配置。

产物位于 `build/libs/AE2-Light-UI-Compat-Forge-1.20.1-1.0.1.jar`。将它放入客户端 `mods` 文件夹，并启用配套资源包。不要同时安装旧版本。

在 IntelliJ IDEA 中打开本目录，按 Gradle 工程导入。常用命令：

```powershell
.\gradlew.bat genIntellijRuns
.\gradlew.bat runClient
.\gradlew.bat verifyMixins
```

`runClient` 使用 Forge 47.4.0 和 AE2 15.4.11。`verifyMixins` 在开发映射环境下对实际 Minecraft/AE2 类执行 Mixin 注入及 ASM 字节码检查，并由 `build` 的 `check` 阶段自动运行；它不能替代实际游戏画面检查。正式 JAR 由 ForgeGradle 重映射，Mixin 引用由注解处理器生成 refmap。

## 工程结构

```text
AE2-Light-UI-Compat/
├── build.gradle                 # ForgeGradle、Mixin、依赖和验证任务
├── settings.gradle              # 插件仓库和工程名称
├── gradle.properties            # Minecraft/Forge/模组版本
├── gradlew / gradlew.bat         # Gradle Wrapper
├── gradle/wrapper/
├── src/main/java/local/ae2lightuicompat/
│   ├── AE2LightUICompat.java     # 模组入口
│   ├── ClientTheme.java         # 专用按钮贴图及文字颜色
│   ├── ScopedButton.java        # AE2 控件归属标记
│   └── mixin/
├── src/main/resources/
│   ├── META-INF/mods.toml       # 游戏内显示名称、依赖等
│   ├── ae2lightuicompat.mixins.json
│   └── pack.mcmeta
├── src/test/java/               # 实际 Mixin 注入验证
└── src/test/resources/          # 测试服务配置
```

`build/`、`.gradle/` 和 `run/` 为本地生成目录，不提交到 Git。此前的 Python 文件用于手动下载依赖、调用 javac 和验证 JAR，不参与模组运行；现在这些工作已交由 Gradle 完成，旧脚本可在 Git 历史中查阅。

## 名称与兼容性

- 游戏内显示名：`AE2 浅色界面适配 (AE2 Light UI Compat)`，定义在 `src/main/resources/META-INF/mods.toml`。
- Java 入口类：`local.ae2lightuicompat.AE2LightUICompat`。
- JAR 名称：`AE2-Light-UI-Compat-Forge-1.20.1-<版本>.jar`。
- 内部模组 ID：继续使用 `ae2isolatedui`，定义在入口类及 `mods.toml`；这是兼容已有资源包的稳定标识，不是游戏内显示名称。

资源包须提供 `assets/ae2isolatedui/textures/gui/widgets.png`，布局沿用 Minecraft 1.20.1 按钮贴图区域。普通文字颜色为 `#413F54`，禁用文字颜色为 `#D2D3DF`。本仓库不包含第三方贴图。

## 参考与许可

工程基于 [Forge 1.20.1 官方 MDK](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html)，Mixin 构建配置参照 [SpongePowered 官方说明](https://github.com/SpongePowered/Mixin/wiki/Mixins-on-Minecraft-Forge)。模组代码采用 MIT 许可；第三方工具和资源遵循各自许可。
