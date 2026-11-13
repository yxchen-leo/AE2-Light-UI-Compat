# 本地开发依赖

这里的清单记录 2026-11-06 本机文件名、内部版本和 SHA-256；不是自动下载清单，也不表示每个文件都是必需依赖。

`declarations.json` 列出构建文件中的本地引用，包含可选测试分支。`libs.lock.json`（若存在）记录全部现存 libs JAR，可能含历史版本。

第三方 JAR 不纳入 Git。优先使用项目已有的依赖准备脚本；当前脚本不保证补齐所有构建依赖。下载地址尚未核实的条目保持为空；项目主页不能当作下载地址。

可使用整合包仓库的 scripts/verify_dependencies.py 校验清单与本地依赖目录；工具只读，不会修改文件。
原有 dependencies-sha256.json 保留不变；本次没有改动构建依赖版本，也没有执行构建。

## 仓库内校验入口

本仓库已包含 `dependencies/verify.py`（Python 3.11+），无需另取整合包仓库。有 libs 清单的工程可运行 `python dependencies/verify.py dependencies/libs.lock.json libs --strict`。校验工具只读目标目录；更多说明见根目录 `COLLABORATION.md`。
