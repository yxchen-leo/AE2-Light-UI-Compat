# AE2 Light UI Compat · AE2 浅色界面适配

用于 **Minecraft 1.20.1 / Forge 47.x / AE2 15.x** 的客户端按钮适配模组。

配合浅色资源包，为 AE2 控件体系注册的通用按钮提供独立贴图和文字颜色，例如机器优先级的加减按钮，同时保持原版菜单和其他模组的按钮样式。资源包关闭或未提供专用贴图时，按钮恢复原样。

## 安装与资源包

将 `AE2-Light-UI-Compat-Forge-1.20.1-v1.0.0.jar` 放入客户端实例的 `mods` 目录，并启用配套资源包。服务端无需安装。

资源包须提供以下路径，图片布局沿用 Minecraft 1.20.1 `widgets.png` 的按钮区域：

```text
assets/ae2isolatedui/textures/gui/widgets.png
```

普通文字颜色为 `#413F54`，禁用文字颜色为 `#D2D3DF`。本仓库只包含适配模组代码，不包含合并材质包或第三方贴图。

旧名称为 `AE2 Isolated Light Buttons`。旧版与新版的模组 ID 都是 `ae2isolatedui`，只安装其中一个 JAR。保留该 ID 是为了兼容已有资源包。

## 构建

当前脚本针对 Windows，使用 Python 3.10+ 和 **JDK 17 或更高版本**。所有输出均使用 Java 17 字节码。无需 Python 第三方包。

```powershell
$env:JAVA_HOME = 'C:\path\to\jdk-17'
python prepare_dependencies.py
python build_mod.py
python verify_mod.py
```

`prepare_dependencies.py` 从 Forge、Mojang 和 Modrinth 官方分发地址准备固定版本的构建/验证依赖，并校验下载哈希。它只写入本项目 `.deps` 目录，通过 Forge 官方安装器在该目录生成运行时依赖，不修改已有游戏实例。

构建产物：`dist/AE2-Light-UI-Compat-Forge-1.20.1-v1.0.0.jar`。

此项目直接使用 Forge 1.20.1 的 **SRG 运行时方法名**，Mixin 设置 `remap = false`；不应把这些源码直接放入采用 Mojang 开发映射的 Gradle 工程并再次混淆。

也可使用已有依赖，跳过下载脚本：

| 环境变量 | 含义 |
| --- | --- |
| `AE2_BUILD_JAVA_HOME` | 优先于 `JAVA_HOME` 的 JDK 路径 |
| `AE2_FORGE_DIR` | Forge 47.4.0 客户端安装目录，包含 `libraries` |
| `AE2_EXTRA_LIB_DIR` | Minecraft 1.20.1 的额外运行库目录，递归读取 JAR |
| `AE2_JAR` | 用于离线验证的 AE2 Forge 15.4.11 JAR |

## 验证范围

已针对 **Forge 47.4.0、AE2 15.4.11、Mixin 0.8.5** 构建并执行离线转换验证：

- 对实际 Minecraft `AbstractButton` 和 AE2 `WidgetContainer` 类应用 Mixin。
- 检查 3 个注入点，并对转换后的类执行 ASM 字节码分析。
- 不把测试代码或游戏依赖打入模组 JAR。

没有进行游戏内实测；其他 Forge 47.x / AE2 15.x 组合尚未逐一验证。独立绘制按钮、未通过 AE2 控件容器注册控件的附属界面不在此适配范围内。

## 项目结构

```text
src/main/java/       模组与 Mixin 源码
src/main/resources/ Forge 元数据与 Mixin 配置
src/test/java/       离线 Mixin / ASM 验证工具
prepare_dependencies.py  固定版本依赖准备
build_mod.py             构建模组
verify_mod.py            验证注入点
```

## 许可

适配代码采用 [MIT License](LICENSE)。本项目不包含 Minecraft、Forge、AE2 或原资源包资产，也不改变它们各自的许可。
