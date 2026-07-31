# Laowu Meme — Forge 1.20.1

这是基于原版 **laowu meme** 修改的 Minecraft **1.20.1 Forge** 移植版。

## 修改内容

- 将原 Fabric / Minecraft 26.1.2 工程移植到 Forge 1.20.1
- 改为 Java 17 与 Forge 47.4.0
- 保留服务端权威的猫配对、靠近、锁定和右键打断逻辑
- 移植猫歪头、弓背、体型放大动画
- 移植多人网络同步
- 支持从 `config/laowu_meme/sounds/` 直接加载外部 OGG
- 支持中文或带空格的 OGG 文件名
- 在 Forge Mods 页面加入音频配置界面，可刷新、分页和逐条启用/禁用

## 玩法说明

一只命名为"老吴"的猫和 6 格内任意一只猫触发：

两只猫先走到一起、身体面对面锁定，头各自歪头（镜像歪头）+ 弓背哈气姿势 + 体型放大

随机循环播放选定的 BGM 之一（16 格内玩家听到）

玩家右键任意一只猫打断，两只猫自然走开

被打断的这对猫 3 分钟内不再触发

## 使用

把下载好的 JAR 放入 Minecraft 1.20.1 Forge 客户端和服务端的 `mods` 文件夹。

多人游戏需要服务端和客户端都安装。服务端负责猫的行为与同步，客户端负责动画和声音。

外部音乐必须是 Ogg Vorbis 格式，放入：

```text
config/laowu_meme/sounds/
```

原源码压缩包没有附带 `laowu2.ogg`、`qiliang.ogg` 和 `zhanhou.ogg`，因此本移植版也没有重新分发这些音频。没有文件的内置条目会在配置界面显示为不可用。

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
- Mod version 1.2.1-forge

## License

MIT。原作者与贡献者信息见 `CONTRIBUTORS.md`。
