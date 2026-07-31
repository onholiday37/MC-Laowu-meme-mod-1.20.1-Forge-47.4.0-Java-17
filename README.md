# Laowu Meme — Forge 1.20.1

这是基于原版 **laowu meme** 修改的 Minecraft **1.20.1 Forge** 移植版。

## 修改内容

- 将原 Fabric / Minecraft 26.1.2 工程移植到 Forge 1.20.1
- 改为 Java 17 与 Forge 47.4.0
- 保留服务端权威的猫配对、靠近、锁定和右键打断逻辑
- 移植猫歪头、弓背、体型放大动画
- 移植多人网络同步
- 内置原作者 v1.2.1 Release 中的 `laowu2`、`qiliang` 和 `zhanhou` 三段 OGG，安装 JAR 后可直接播放
- 支持从 `config/laowu_meme/sounds/` 直接加载外部 OGG
- 支持中文或带空格的 OGG 文件名
- 在 Forge Mods 页面加入音频配置界面，可刷新、分页和逐条启用/禁用

## 使用

把构建出的 JAR 放入 Minecraft 1.20.1 Forge 客户端和服务端的 `mods` 文件夹。

多人游戏需要服务端和客户端都安装。服务端负责猫的行为与同步，客户端负责动画和声音。

JAR 已经内置三段原版音频，无需额外导入。若要添加其他音乐，外部音乐必须是 Ogg Vorbis 格式，放入：

```text
config/laowu_meme/sounds/
```

三段内置音频提取自原作者 [Rogic460/Minecraft-laowu-meme v1.2.1 官方 Release](https://github.com/Rogic460/Minecraft-laowu-meme/releases/tag/v1.2.1)。外部音频导入和启用/禁用配置功能仍然保留。

## 构建

需要 JDK 17：

```bash
./gradlew build
```

成品位于 `build/libs/`。

## 版本

- Minecraft 1.20.1
- Forge 47.4.0
- Java 17
- Mod version 1.2.2-forge

## License

MIT。原作者与贡献者信息见 `CONTRIBUTORS.md`。
