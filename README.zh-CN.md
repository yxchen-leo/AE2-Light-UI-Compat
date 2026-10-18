# AE2 Light UI Compat · AE2 浅色界面适配

[English](README.md) | **简体中文**

适用于 **Minecraft 1.20.1 / Forge** 的 AE2 客户端按钮适配模组。为通过 AE2 控件体系注册的按钮提供独立的浅色贴图和清晰的文字颜色，避免全局替换 Minecraft 通用按钮贴图。

[已发布版本](https://github.com/yxchen-leo/AE2-Light-UI-Compat/releases) · [反馈问题](https://github.com/yxchen-leo/AE2-Light-UI-Compat/issues)

当前源码版本为 **1.1.2**，配套资源包为 **AE2 & Addons Texture Backport 1.1.2**。产物位于 `build/libs/`，也可从 [1.1.2 Release](https://github.com/yxchen-leo/AE2-Light-UI-Compat/releases/tag/v1.1.2) 下载。

1.1.0 新增 1.21.1 风格的左侧工具栏：连续底板连接主面板，按可见按钮数量伸缩；按钮背景为 18×20，排列间隔为 6，支持悬停下沉和键盘焦点样式。适配通过 `VerticalButtonBar` 注册的普通图标按钮；半尺寸按钮保留原绘制。按钮操作与 16×16 点击区域保持 AE2 原有行为。

新增资源为 `assets/ae2lightuicompat/textures/gui/vertical_buttons_bg.png` 和 `toolbar_buttons.png`。两张贴图同时存在时启用侧栏适配，资源重载后重新判定。资源包将公共侧栏设为 `left: 3, top: 1`，使底板覆盖主面板边缘；合成状态页保留独立定位。

1.1.0 已通过 Forge 47.4.0 / AE2 15.4.11 构建和四个目标类的离线 Mixin 注入检查；尚未在游戏内验收画面。

1.1.1 修正：文字按钮使用高版本的无阴影文字与普通/悬停/禁用配色；CPU 列表侧栏右移 1 像素；统一包裹整个侧栏按钮渲染调用，覆盖水晶科技的独立按钮类与闪电科技追加的状态图标。仅适配注册到 AE2 侧栏的普通 16×16 按钮。六个目标类的离线检查不能代替游戏内复验。

1.1.2 修复 AE2 与兼容附属子页面的返回箭头、优先级入口图标、页签背景和图标偏移，配套资源包修正无线设置/垃圾桶页面定位。专用 `tab_buttons.png` 存在时启用；移除后恢复原有物品图标和渲染。详见 [GUI 核对报告](docs/1.1.2-GUI-audit.md)。

## 为什么需要这个模组

将高版本 AE2 界面材质移植到 Minecraft 1.20.1 时，部分控件仍使用 Minecraft 的通用按钮渲染。例如，机器优先级界面的 `+1`、`+5` 等按钮可能在周围界面变为浅色后依然保持旧样式。如果通过资源包直接替换通用按钮贴图，游戏其他界面的按钮也会改变。

本模组会标记通过 AE2 控件容器注册的按钮，让这些按钮读取独立贴图。配套资源包因此可以单独调整它们的样式，同时保留其他按钮的原有外观。

## 功能与适用范围

- 为受支持的 AE2 按钮提供独立浅色贴图，包括优先级调整按钮。
- 使用高版本普通、悬停、禁用配色与无阴影文字，并保留透明度；仅有旧文字按钮贴图时保留旧调色方案。
- 专用贴图不存在时自动恢复原样，包括关闭资源包并完成资源重载之后。
- 只需客户端安装，服务端无需安装。

本模组是 **AE2 & Addons Texture Backport（AE2 及附属高版本材质移植）** 的按钮适配组件。GUI、物品、方块和动画材质由独立资源包提供；这个 JAR 不包含资源包，也不会单独替换全部 AE2 界面。

附属模组的控件若使用 AE2 的控件注册体系，也可能得到适配。自行创建的按钮或使用独立渲染方式的控件不会自动被覆盖，因此不承诺兼容所有附属模组。

## 运行要求

| 组件 | 支持目标 |
| --- | --- |
| Minecraft | 1.20.1 |
| 模组加载器 | Forge 47.x |
| Applied Energistics 2 | 15.x，并安装其必需依赖 |
| 资源包 | AE2 & Addons Texture Backport，或提供专用按钮贴图的其他资源包 |
| 安装位置 | 仅客户端 |

构建和离线注入验证使用 **Forge 47.4.0 / AE2 15.4.11**。此版本不支持 Fabric、NeoForge 或其他 Minecraft 版本。

## 安装方法

1. 安装 Minecraft 1.20.1 对应的 Forge、AE2 及其必需依赖；AE2 15.4.11 包括 GuideME 依赖。
2. 使用本地 `build/libs/` 内的产物，或从 [Releases](https://github.com/yxchen-leo/AE2-Light-UI-Compat/releases) 下载 `AE2-Light-UI-Compat-Forge-1.20.1-1.1.2.jar`。
3. 将 JAR 放入客户端游戏实例的 `mods` 文件夹。
4. 单独安装配套资源包，并在游戏的资源包设置中启用。资源包不包含在本仓库的 Release 附件中。
5. 更新时移除旧版适配模组，包括旧名称 `AE2-Isolated-Light-Buttons` 的 JAR，只保留一个版本。

如果未提供专用按钮贴图，模组会保留原来的按钮渲染。游玩时不需要 Gradle 或项目源码。

## 常见问题

- **按钮没有变色：** 确认已启用兼容资源包，且包含下方列出的专用贴图。检查更高优先级的资源包是否覆盖了该贴图，然后使用 `F3 + T` 重载资源。
- **只有部分附属模组按钮改变：** 本模组作用于 AE2 控件体系中的按钮；自定义按钮可能采用其他渲染方式。
- **提示重复模组或缺少依赖：** 移除旧版 JAR，并核对 Minecraft、Forge、AE2 及 AE2 必需依赖的版本。

反馈问题时，请提供相关版本、启用的资源包及其顺序、出现问题的界面、复现步骤，以及截图或相关日志片段。分享日志前请移除个人信息。

## 资源包作者说明

请按 Minecraft 1.20.1 `widgets.png` 的按钮布局提供以下文件：

```text
assets/ae2lightuicompat/textures/gui/widgets.png
```

完整侧栏主题启用时，文字颜色为普通 `#F2F2F2`、悬停 `#517497`、禁用 `#413F54`，不绘制阴影。仅提供 `widgets.png` 的旧主题保留 `#413F54` / `#D2D3DF` 配色；两者均保留透明度。

从 1.0.2 起，内部模组 ID 和贴图命名空间统一为 `ae2lightuicompat`。请配合资源包 1.0.3 或更新版本使用，升级前移除所有旧版适配模组 JAR；不再使用旧 ID 和贴图路径。它们与游戏内显示名称 **AE2 浅色界面适配 (AE2 Light UI Compat)** 是不同概念。Java 入口类为 `local.ae2lightuicompat.AE2LightUICompat`。

## 构建与开发

使用 JDK 17，并设置 `JAVA_HOME`。这是标准 ForgeGradle 工程，无需 Python。Gradle Wrapper 会下载项目指定的 Gradle，工程也包含 Java 工具链自动下载配置。首次构建需要联网。

Windows：

```powershell
.\gradlew.bat build
```

Linux/macOS：

```sh
./gradlew build
```

产物位于 `build/libs/AE2-Light-UI-Compat-Forge-1.20.1-1.1.2.jar`。

在 IntelliJ IDEA 中按 Gradle 工程导入。常用开发命令：

```powershell
.\gradlew.bat genIntellijRuns
.\gradlew.bat runClient
.\gradlew.bat verifyMixins
```

`runClient` 包含 Forge 47.4.0、AE2 15.4.11 和 GuideME 20.1.15。要查看浅色按钮效果，请把兼容资源包放入开发客户端的 `run/resourcepacks` 文件夹并启用。

`verifyMixins` 会对 Minecraft 和 AE2 的开发环境类执行真实 Mixin 注入，并使用 ASM 检查生成的字节码；`build` 会自动执行该任务。发布 JAR 由 ForgeGradle 重映射，Mixin 引用映射由注解处理器生成。

1.0.2 已通过构建，以及开发环境和 Forge 47.4.0 / AE2 15.4.11 生产运行时类的离线注入检查。这些检查不能代替游戏画面验收，本次发布验证没有启动 Minecraft。

```text
├── build.gradle                 # 依赖、ForgeGradle、Mixin 和验证任务
├── settings.gradle
├── gradle.properties            # Minecraft、Forge 和模组版本
├── gradlew / gradlew.bat
├── gradle/wrapper/
├── src/main/java/local/ae2lightuicompat/
├── src/main/resources/          # mods.toml、Mixin 配置及资源元数据
├── src/test/java/               # 离线 Mixin 验证
└── src/test/resources/          # 测试服务配置
```

`build/`、`.gradle/` 和 `run/` 在本地生成，不提交到 Git。早期使用的 Python 构建辅助脚本可在 Git 历史中查阅。

## 开发说明与许可

本项目的实现、构建配置和文档使用了 OpenAI Codex 提供的大量生成式 AI 辅助，项目起源于用户提出的材质移植适配需求。上述验证说明区分了自动化检查与实际游戏验收。

适配模组代码采用 [MIT 许可证](LICENSE)。第三方工具和资源包材质遵循各自许可；本仓库的许可证不授予独立材质包的再分发权。本项目是独立适配模组，并非 AE2 官方发布。

开发参考：[Forge 1.20.1 MDK](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html)、[SpongePowered Forge Mixin 指南](https://github.com/SpongePowered/Mixin/wiki/Mixins-on-Minecraft-Forge)。
