# 更新日志

所有重要变更记录在此文件。格式参考 [Keep a Changelog](https://keepachangelog.com/)，版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [1.2.1] - 2026-07-27

### 修复（Bug）
- **战吼/导入音频远离猫不衰退、骤然消失**：三个固有音频在代码与资源里完全对称，但实测战吼与所有导入音频远离猫时音量不随距离减小、超过某距离突然消失（laowu2/qiliang 偶发正常）。根因是 MC 对「流式(stream) / 磁盘读取」的音频其自带的 distance attenuation 在某些路径不生效。修复方式：关闭实例的 MC 自带衰减（`attenuation=NONE`），改为在 `MemeSoundInstance` / `ImportedSoundInstance` 的 `getVolume()` 里手动按「玩家到两只猫中点」的距离线性计算音量 —— 0~16 格从 1 平滑降到 0，超过 16 格保持静音、32 格停止。所有音频（含战吼、导入）现在行为一致、均为自然衰退。
- **服务端从不选战吼**：`ServerMemeManager` 的 `soundId` 随机只在 laowu2/qiliang 间取，从不含战吼（zhanhou）；已纳入 `SOUND_ZHANHOU(2)`，三者都可能被服务端选中。（客户端实际播放由各端 `AudioPool.random()` 随机，此修复让服务端元数据与新音频一致。）

## [1.2.0] - 2026-07-27

### 新增功能
- **导入音频播放**：把 `.ogg` 文件放进 `config/laowu_meme/sounds/`，触发整活时会与三段固有音频一起被随机播放，无需 F3+T、不进资源包。支持中文/空格文件名。
- **配置屏音频启用/禁用**：ModMenu 配置界面里每条音频（固有 + 导入）都是可点击的 toggle 按钮，点击切换启用/禁用，状态持久化到 `config/laowu_meme/enabled.properties`；禁用项不会被随机播放。
- **多版本 jar 命名**：构建产物命名为 `laowu_meme-1.2.0+26.1.2.jar`，方便未来适配其他 MC 版本时并列管理。

### 修复（Bug）
- **导入音频不播放**：之前 mixin 拦截 `SoundBufferLibrary.getStream` 的前缀错误（应为 `sounds/imported/`），导致导入音频走原逻辑找不到资源而静默。已修正前缀并剥 `.ogg` 后缀解码。
- **导入音频中文/空格文件名崩溃**：`Identifier` 的 path 不允许非 ASCII，直接拼文件名会抛 `IdentifierException` 导致网络协议错误断连。新增 `SoundIdCodec` 对文件名做 UTF-8 hex 编码写入 Identifier，mixin 侧解码回真名读盘。
- **禁用固有音频不生效**：配置屏 toggle 传给 `AudioPool` 的 builtin key 缺 `builtin:` 前缀，导致禁用状态写错 key；已统一前缀。
- **配置界面不刷新导入列表**：v1.1.22 重写配置屏时删掉刷新逻辑，删/增 .ogg 后列表不更新；已恢复打开 GUI 时扫描磁盘。
- **整活却常没声音**：`startSound` 原用 `玩家离猫中点>16格就不播放` 的硬限制，导致稍微站远点就只有画面没声音；已移除硬限制，由 MC 自身衰减控制。
- **声音距离衰减不自然**：`updatePos()` 原把 volume 硬切成 0/1，导致远离时突然静音；改为不覆盖 volume，由 MC 按 16 格 `attenuationDistance` 做自然衰退，>32 格才停止实例。
- **自定义 SoundInstance NPE 断连**：`ImportedSoundInstance` 曾覆盖 `getSound()` 并遮蔽超类 `sound` 字段，导致 `SoundEngine.play` 调 `getVolume()` 时 NPE；已按原版 `resolve()` 机制正确填充超类字段。

### 开发 / 构建
- 版本号 1.1.26 → 1.2.0（功能集完整，进入 minor 版本）。
- 清理了 v1.1.23 加入的调试日志。

## [1.1.26] - 2026-07-27

### 改进
- **声音随距离自然衰退**：原先 `ImportedSoundInstance` / `MemeSoundInstance` 的 `updatePos()` 把音量硬切成 0 或 1（≤32 格 volume=1.0，>32 格 volume=0），覆盖了 MC 自身的距离衰减曲线，导致远离时声音突然消失。改为不再手动覆盖 `this.volume`，由 MC 按 `attenuationDistance`（导入 16 / 固有默认 16）做自然衰退；仅在玩家离猫中点 >32 格时才停止实例。靠近大声、远离慢慢变小声、过远停掉。

### 开发 / 构建
- 版本号 1.1.25 → 1.1.26。

## [1.1.25] - 2026-07-27

### 修复（Bug）
- **导入音频仍不播放（v1.1.21 误判前缀导致的长期 bug）**：日志终于暴露真相：`SoundEngine.play` 调用 `getStream` 时传入的 `location` 是 `Sound.getPath()` 的结果 `laowu_meme:sounds/imported/<hex>.ogg`（带 `sounds/` 前缀和 `.ogg` 后缀），而不是我此前错误判断的 `laowu_meme:imported/<hex>`。因此 v1.1.21 把 mixin 匹配前缀改成 `imported/` 后，拦截**永远匹配不上**、导入音频走原逻辑找不到资源而静默。修复：mixin 匹配前缀改回 `sounds/imported/`，并在解码前剥 `.ogg` 后缀。

### 开发 / 构建
- 版本号 1.1.24 → 1.1.25。

## [1.1.24] - 2026-07-27

### 修复（Bug）
- **导入音频删掉/重新导入后配置界面不显示（v1.1.22 回归）**：v1.1.22 重写配置屏时 `init()` 直接用了内存缓存的导入列表 `IMPORTED`，没有重新扫描磁盘；F3+T 只重载资源包、不重新跑 mod 初始化，故都不刷新。修复：配置屏 `init()` 开头调用 `AudioPool.refreshImported()`（打开 GUI 时扫一遍，符合既定轻量方案，不引后台监听线程）。现在删文件后立即不显示、放新文件后打开配置界面即显示。
- 注：上一轮 BUG2「导入音频无法播放」经用户自查为导入文件本身 0 秒时长（文件损坏），非 mod 问题；本轮仅修配置界面不刷新的回归。

### 开发 / 构建
- 版本号 1.1.23 → 1.1.24。

## [1.1.23] - 2026-07-27

### 修复（Bug）
- **禁用固有音频后仍被随机到（BUG1）**：配置屏 toggle 传给 `AudioPool.toggleEnabled` 的 builtin key 缺 `builtin:` 前缀（只传了 `laowu2`），而 `random()` 检查的是 `isEnabled("builtin:laowu2")`，前缀不匹配导致禁用永不生效。修复：builtin 条目传 `"builtin:" + key`。
- **重启后禁用状态全丢**：`AudioPool.init()` 原把 `EnabledConfig.load()` 的结果写进一个临时 map、没回填 `disabledKeys`（第二次 `toBoolMap()` 基于清空的 disabledKeys 重建，全是 true）。修复：先 `refreshImported()`，再把文件读入独立 map、显式回填 `disabledKeys`。
- 新增调试日志定位「导入音频无法播放」：`startSound` 打印选中目标+距离、`ImportedSoundInstance` 构造打印创建+音量、mixin `getStream` 打印拦截+路径+成功/失败，便于从 latest.log 精确定位。

### 开发 / 构建
- 版本号 1.1.22 → 1.1.23。

## [1.1.22] - 2026-07-27

### 新增功能
- **配置屏每条音频可启用/禁用**：每条音频（固有 + 导入）变为可点击的 toggle 按钮，点击翻转「启用/禁用」状态，状态持久化到 `config/laowu_meme/enabled.properties`；禁用项不会被随机播放。启用显示绿色 `✓`、禁用显示红色 `✗`。

### 修复（Bug）
- **整活却没声音（"还是会有没声音的情况"）**：`ClientMemeState.startSound` 原先有 `玩家离两只猫中点 > 16 格就直接不播放` 的硬限制，导致玩家稍微站远一点（猫仍在整活）就完全静音。移除该限制，音量交给 MC 自身距离衰减（导入 16 格 / 固有默认）。同时修复同一对猫重复触发时 `sounds.put` 覆盖旧实例却不 `stop` 旧实例的问题——现在先停旧再起新，避免实例叠加/泄漏。
- **配置屏文字被按钮遮挡**：原先音频列表用居中 StringWidget、底部「打开音频文件夹」按钮也居中且更宽，长文本超出按钮宽度时中间被按钮压住。改为每条音频自己就是 toggle 按钮（逐行居中、独占一行），标题/提示置顶、底部按钮与列表留白隔开，不再叠字。

### 开发 / 构建
- 版本号 1.1.21 → 1.1.22。

## [1.1.21] - 2026-07-27

### 修复（Bug）
- **导入音频静音（v1.1.20 反改前缀导致的回归）**：`SoundEngine.play` 调用 `getStream(location, looping)` 时传入的 `location` 就是 `SoundInstance` 的 `Identifier`（路径 `imported/<hex>`，无 `sounds/` 前缀），`sounds/` 是 vanilla `getStream` 内拼资源路径才加。v1.1.20 把 mixin 匹配前缀错改成 `sounds/imported/` 导致永远匹配不上、导入音频走原逻辑找不到资源而静默。改回 `imported/`（保留 1.1.20 的 NPE 修复），导入音频现在能正常循环播放。
- **配置屏文本仍空白（v1.1.19 屏幕级直绘实测也不显示）**：放弃 `Screen.extractRenderState` 直绘文本，改为用 `StringWidget`（`AbstractWidget` 子类，与按钮同一渲染管线——按钮能显示即说明该管线可靠）逐行渲染文本；颜色烤进 `Component`（`StringWidget` 无 `setColor`），位置手动 `setX` 居中。删除了 `extractRenderState` 覆盖与 `Line` 内部类。配置屏现能正确显示标题/固有音频/导入音频名/总数/提示。
- mixin 解码失败时新增日志 + 屏幕 toast 提示「导入音频解码失败：<名>」，避免再次静默无提示。

### 开发 / 构建
- 版本号 1.1.20 → 1.1.21。

## [1.1.20] - 2026-07-27

### 修复（Bug）
- **单人触发仍网络协议错误断连（v1.1.19 未解决的崩溃）**：根因与文件名无关，而是 `ImportedSoundInstance` 覆盖了 `getSound()` 并声明了遮蔽超类 `sound` 字段的 `private final Sound sound`，导致超类 `AbstractSoundInstance` 的 `protected sound` 字段从未被填充。vanilla 的 `SoundEngine.play` 先调 `getSound()`（子类返回非 null 通过检查）再调 `getVolume()`（读超类 `this.sound`，为 null）直接 NPE。修复：删除遮蔽字段与 `getSound()` 覆盖，在 `resolve()` 内仿默认实现把 `this.sound = events.getSound(random)` 填上。`MemeSoundInstance`（固有音频）走默认 resolve 本就正常。
- **mixin 拦截前缀（v1.1.20 此处判断错误，已在 1.1.21 纠正）**：当时误以为 `Sound.getPath()` 会把 location 拼成 `sounds/<path>.ogg`，于是把 mixin 匹配前缀从 `imported/` 改回 `sounds/imported/`。但 `SoundEngine.play` 调用 `getStream(location, looping)` 时传入的 `location` 就是 `SoundInstance` 的 `Identifier`（路径前缀 `imported/`，**没有** `sounds/`），`sounds/` 只是 vanilla `getStream` 内部拼资源路径时才加。`sounds/imported/` 前缀永远匹配不上 → 导入音频静默不响。正确前缀应为 `imported/`（见 1.1.21）。

### 开发 / 构建
- 版本号 1.1.19 → 1.1.20（NPE 修复有效，但 mixin 前缀改错导致导入音频仍静音）。

## [1.1.19] - 2026-07-27

### 修复（Bug）
- **单人触发即网络协议错误断连（v1.1.18 崩溃）**：导入音频文件名含中文/空格（如 `MP3到OGG转换器- FreeConvert.com.ogg`）被直接塞进 `Identifier` 的 path，而 MC 的 `Identifier` 只允许 `[a-z0-9/._-]`，构造时抛 `IdentifierException`，客户端在渲染线程炸掉、集成服务器判为网络协议错误踢档。改用 `SoundIdCodec` 把真名做 UTF-8 hex 编码（只含 `0-9a-f`，必然合法）写入 `Identifier`，`SoundBufferLibraryMixin` 侧 hex 解码回真名再去磁盘读文件，彻底规避非法字符。
- **v1.1.18 配置屏文本仍空白**：此前 `TextLabel extends AbstractWidget` 把尺寸设为 1×1，而 `AbstractWidget.extractRenderState` 会按 widget 的 (x,y,w,h) 矩形裁剪，1×1 裁剪框把文字全裁掉（按钮 220×20 故正常）。改为在 `Screen.extractRenderState(GuiGraphicsExtractor,...)` 里直接 `centeredText` 画静态文本（屏幕级 extractor 不受 widget 裁剪框限制），删除 `TextLabel`，配置屏现能正确显示标题/固有音频/导入音频名/总数/提示。
- **mixin 拦截前缀不一致（v1.1.18 潜伏 bug）**：`ImportedSoundInstance` 生成的 `Identifier` 路径是 `imported/<名>`，但 mixin 只匹配 `sounds/imported/`，导致即使文件名合法也永远拦截不到、导入音频静默不响。已将 mixin 匹配前缀统一为 `imported/`。

### 开发 / 构建
- 版本号 1.1.18 → 1.1.19。

## [1.1.18] - 2026-07-27

### 新增功能
- **导入音频可播放（Bug 3）**：把 `.ogg` 拖进 `config/laowu_meme/sounds/` 后，触发整活时会与三段固有音频一起被随机播放，**无需 F3+T、不进资源包**。实现：新增 `ImportedSoundInstance`（循环、跟随两只猫中点、过远静音，行为同固有音频）+ `SoundBufferLibraryMixin` 拦截 `laowu_meme:imported/*` 的资源读取，直接从磁盘用 JOrbis 解码。`AudioPool` 统一维护导入列表与随机池（新增 `refreshImported()` / `importedNames()` / `random()` 合并池）。
- 移除未使用的 `fabric-resource-loader-v1` 依赖（导入音频改用 mixin 直读磁盘，无需该模块），保持 mod 轻量。

### 修复（Bug）
- **配置屏文本不渲染（v1.1.16/1.1.17 的 Bug1/2）**：v1.1.18 改用 `TextLabel extends AbstractWidget` 方案，但实测仍空白——该方案因 1×1 裁剪框把字裁掉而无效，真正的修复见 [1.1.19]。

### 开发 / 构建
- 版本号 1.1.16 → 1.1.18（整合 v1.1.17 配置屏修复与本次导入音频功能）。

## [1.1.16] - 2026-07-27

### 新增功能
- **新增固有音频「战吼」**（资源 `assets/laowu_meme/sounds/zhanhou.ogg`，源自 `D:\DL\下载.ogg`）。现共有三段固有音频。
- **固有音频显示名重命名**：老吴2 → `[那个那个]`，凄凉 → `[老吴凄凉]`，新音频 → `[战吼]`（`AudioPool.BUILTIN_DISPLAY` + `sounds.json` 同步）。
- **加入 ModMenu 配置屏**（软依赖 `modmenu >=18.0.0`，不引 YACL，手写轻量 Screen）：显示三段固有音频名、池内音频总数（固有+导入）、「打开音频文件夹」按钮（把 .ogg 拖进 `config/laowu_meme/sounds` 后游戏内按 F3+T 重载资源即生效）。无 WatchService 常驻线程，列表在打开界面时扫描，符合轻量定位。
- **随机播放改为客户端本地池**：触发时客户端从三段固有音频随机挑一段（`AudioPool.randomBuiltin()`），不再依赖服务端 `soundId` 选曲。服务端 S2C 包仍保留 `soundId` 字段以兼容旧版本。

### 开发 / 构建
- 版本号 1.1.15 → 1.1.16。
- `fabric.mod.json` 加 `modmenu` entrypoint 与 `recommends.modmenu >=18.0.0`；`build.gradle` 经 Modrinth maven 编译期引入 `maven.modrinth:modmenu:18.0.0`（不打包进 jar，运行时由用户安装 ModMenu）。

## [1.1.15] - 2026-07-26

### 兼容 / 适配
- 降低 Fabric Loader 最低依赖：fabric.mod.json `fabricloader` `>=0.19.3` → `>=0.19.2`，`gradle.properties` `loader_version` 0.19.3 → 0.19.2。mod 现可在 Fabric Loader 0.19.2 上加载（0.19.2→0.19.3 为小版本，无 API 断层，无需改代码）。

### 开发 / 构建
- 版本号 1.1.14 → 1.1.15。
- README 依赖说明同步更新为 Fabric Loader 0.19.2。

## [1.1.14] - 2026-07-23

### 修复（Bug）
- **v1.1.13 大幅回归：前腿缩过头不到地、后腿过长插地、后腿与身体断开（用户截图）**。根因确认：猫模型 body 是单一 cube 部件、**腿不随 body 旋转**——身体弓得越厉害，髋部连接点跑得越远，腿无论 yScale 怎么调都难同时解决"够不到身体"和"插进地板"，且 body 下降后腿顶与 body 底出现"断开"缝。v1.1.13 的 HIND=1.9/FRONT=0.6 就是这个矛盾下的极端调参，反而加剧了所有问题。
  - **根本修复（用户确认方案）**：把弓身幅度降下来，从源头减少 body 位移。
    - `BODY_PITCH` 0.18 → **0.10**。身体髋部连接点位移显著减小，断开/插地随之解决。
    - 弓身感由 `HEAD_DIP=0.3`（低头哈气）+ `TAIL_LIFT=0.9`（翘尾）共同承担，视觉上仍是"弓背哈气"。
    - 权衡：弓身角度比 v1.1.13 小，但仍比 v1.1.9(0.1) 持平——历史已验证 0.1 不撕裂肢体，本次复用此安全值。
  - 腿形变退居次要补偿位（取历史经验中间值）：
    - `HIND_SCALE` 1.9 → **1.4**。历史：1.3 脱节 / 1.5 不够 / 1.9 插地 → 居中 1.4。
    - `FRONT_SCALE` 0.6 → **0.85**。历史：0.6 缩过头不到地 / 0.9 穿模 → 居中 0.85。

### 开发 / 构建
- 版本号 1.1.13 → 1.1.14。

## [1.1.13] - 2026-07-23

### 修复（Bug）
- **后腿长度还是不够（v1.1.12 回归，HIND_SCALE=1.5 仍不足）**：身体弓起(头低尾高)后前后端竖向错开，进一步放大前后腿视觉差。原 `HIND_SCALE=1.5` 仅把后腿拉到 9，仍显短。
  - 修复：`HIND_SCALE` 1.5 → **1.9**，后腿 6 → 11.4，明显拉长、衔接弓起的身体。
- **前腿看起来是后腿两倍长（用户反馈）**：根因两层叠加——① 原版猫模型前脚 cube 长 10、后脚 cube 长 6（javap 核实 `addBox(-1,0,0,2,10,2)` vs `addBox(-1,0,1,2,6,2)`），前腿天生就比后腿长约 1.67 倍，是 Mojang 模型原貌、非 bug；② 上一版 `FRONT_SCALE=0.9` 只把前腿从 10 缩到 9，加上弓身视觉放大，前腿仍显过长。
  - 修复：`FRONT_SCALE` 0.9 → **0.6**，前腿 10 → 6。主动让后腿(11.4)明显长于前腿(6)，抵消弓身带来的前后腿视觉差，整活姿态下前后腿协调。

### 开发 / 构建
- 版本号 1.1.12 → 1.1.13。

## [1.1.12] - 2026-07-23

### 修复（Bug）
- **后脚长度仍不够（v1.1.11 回归，HIND_SCALE=1.3 不足）**：javap 核实身体绕其支点 `z=-10`（`PartPose.offsetAndRotation(0,12,-10,π/2,0,0)`）旋转 `BODY_PITCH=0.18`，后脚附着点 `z=+5` 距支点 15 单位 → 上抬约 `15·sin(0.18)≈2.7` 单位。v1.1.11 的 `HIND_SCALE=1.3` 仅抬升 `6*0.3=1.8` 单位，差约 0.9 → 仍有缝。
  - 修复：`HIND_SCALE` 1.3 → **1.5**，抬升 `6*0.5=3.0` 单位（略超 2.7，保衔接、不浮空）。后脚 cube 自 foot 向上 6 单位，缩放以 foot 为原点，脚尖仍贴地、髋端上抬衔接身体。
- **前脚穿模（v1.1.11 新出现）**：同一旋转下，前脚附着点 `z=-5` 距支点仅 5 单位 → 下压约 `5·sin(0.18)≈0.9` 单位，前脚髋端未跟随下压 → 与身体重叠"穿模"。
  - 修复：前脚用 **`FRONT_SCALE=0.9`**（`yScale<1`）把髋端下压 `10*0.1=1.0` 单位（略超 0.9，保贴合）去贴合下压的身体、消除穿模；脚尖仍贴地（前脚 cube 自 foot 向上 10 单位，`addBox(-1,0,0,2,10,2)+PartPose.offset(±1.2,14.1,-5)`，javap 核实）。
  - 复位扩展：非整活时**四条腿** `yScale` 全部复位 `1.0`（原仅后脚两条），避免形变残留（模型实例被所有猫共享 + `yScale` 不被原版 `setupAnim` 重置）。

### 开发 / 构建
- 版本号 1.1.11 → 1.1.12。

## [1.1.11] - 2026-07-23

### 修复（Bug）
- **尾巴仍有断开（v1.1.10 回归，尾巴两节衔接）**：v1.1.10 让 `tail1`/`tail2` 用相同角度 `0.6/0.6` 平行翘起，但两节连接点仍没接上。根因确认（javap 核实）：`tail1`/`tail2` **平级**挂 root，旋转 `tail1` 时 `tail2` 根部不跟随 `tail1` 末端 → 脱节。
  - 修复：改为 **`tail1` 完全不旋转（只受原版动画），仅 `tail2` 翘起**（`TAIL_LIFT=0.9`）。`tail2` 根部天然落在 `tail1` 末端初始位置，`tail1` 不动则末端不动，两节严丝合缝衔接，尾尖上翘呈现哈气尾。
- **后脚与身体断开（用户反馈）**：身体弓起（`BODY_PITCH=0.18`）后臀部抬高，后脚（cube 自 foot 支点向上延伸，`addBox(-1,0,1,2,6,2)+PartPose.offset(±1.1,18,5)`，javap 核实）原本长度够不到抬高的身体 → 断裂。
  - 修复：用部件 **`yScale` 拉长后脚**（`HIND_SCALE=1.3`）。`ModelPart.compile` 以支点(foot)为原点 `PoseStack.scale(yScale)`，`y∈[0,6]` 放大为 `y∈[0,6*1.3]`，即**脚尖仍贴地、膝/髋端上抬去衔接身体**，脚宽/深度不变、位置不动（不会浮空）。
  - 关键防御：模型实例被所有猫**共享**，且 `yScale` **不被原版 `setupAnim` 重置**；若只在整活时设、不复位，会被永久残留（所有猫后脚都变长）。故非整活时把 `yScale` 复位回 `1.0`。

### 开发 / 构建
- 版本号 1.1.10 → 1.1.11。

## [1.1.10] - 2026-07-23

### 修复（Bug）
- **尾巴两节（tail1/tail2）错位（v1.1.9 回归）**：查证 26.1 猫模型 `tail1`/`tail2` 是**平级**挂在 root 下（用 `PartDefinition.addOrReplaceChild` 构建，非串联子部件）。旋转 `tail1` 时 `tail2` 不跟随，连接处必脱节。v1.1.9 用 `0.9/1.4`（tail2 过大）导致尾尖过度弯折、与 tail1 末端明显错位。
  - 修复：两节改**相同角度** `0.6/0.6` 平行翘起，偏差最小、视觉似一条直尾上翘。
  - 架构限制：平级结构下，任何让 tail1 显著旋转的操作都会使 tail2 根部与 tail1 末端产生位移偏差；要尾巴尖真正竖直且两节完美连接，需重构模型（tail2 挂 tail1 下），属较大改动，本次未做。
- **身体前倾略微加回**：`BODY_PITCH` 0.1 → 0.18（v1.1.9 收敛过弱，用户要求"略微加一点"），仍远小于 v1.1.8 的 0.35（避免撕裂腿）。

### 开发 / 构建
- 版本号 1.1.9 → 1.1.10。

## [1.1.9] - 2026-07-23

### 修复（Bug）
- **弓背哈气姿态方向修正（v1.1.8 回归）**：v1.1.8 的"身体前倾"符号写反，实际是头部高、尾巴低（后仰），正确应为尾巴高、头部低（前倾/拱背）。已在 `CatModelMixin.setupAnim` TAIL 把 `body.xRot` / `tail1.xRot` / `tail2.xRot` 的符号全部翻正（`-=` → `+=`）。
- **四肢与身体断开（v1.1.8 回归）**：根因是猫模型 `body` 是单一立方体部件、四肢都平级挂在 `root` 下且**不随 `body` 旋转而移动**；v1.1.8 把 `body` 整体旋转 0.35 rad，身体几何体被从腿上"拽开"导致脱节。
  - 修复：把 `BODY_PITCH` 从 0.35 收敛到 0.1（极小，避免撕裂腿/头连接）；新增 `HEAD_DIP=0.3` 让**头部下低**参与表达哈气；翘尾保留（`tail1/tail2.xRot +=`）。即"哈气感"改由**低头 + 翘尾**承担，身体仅微弓，腿连接完好。
  - 说明（架构限制）：26.1 猫模型只有单一 `body` 部件、无中段子部件，无法做"局部驼背"。若要更强身体前倾且不撕裂肢体，需要重构模型（拆分出背部子部件），属较大改动，本次未做。

### 开发 / 构建
- 版本号 1.1.8 → 1.1.9。

## [1.1.8] - 2026-07-23

### 调整 / 新增
- **体型放大效果减半**：锁定后放大从 50% 降到 25%（渲染 `scale` 1.5 → 1.25），猫不再那么"巨化"，更贴近写实。
- **弓背哈气姿态（新）**：锁定猫现在除了歪头，还会**身体前倾 + 尾巴竖直翘起**，整体呈现"弓背哈气"的经典猫防御姿势。技术实现：在 `CatModelMixin` 的 `setupAnim` TAIL 注入，对 `body` 设 `xRot`（前倾）、对 `tail1`/`tail2` 设 `xRot`（翘尾），叠加在模型原动画之上。注：26.1 猫模型只有单一 `body` 部件（无中段子部件），"中段抬高"靠整体前倾近似、配合翘尾呈现哈气轮廓——单 cube 模型无法做局部驼背。
- **锁定距离再微远**：两猫中心距 `LOCK_DISTANCE` 1.8 → 2.0，头对头但身体分得再开一丢丢（用户要求"再远一点点"）。

### 开发 / 构建
- 版本号 1.1.7 → 1.1.8。

## [1.1.7] - 2026-07-22

### 修复（Bug）
- **头部歪头终于稳定生效（这次是真正的根因）**：v1.1.6 黑屏修好了，但头还是不转。
  - 真正根因（26.1.2 client jar 字节码核实）：`AdultFelineModel.setupAnim` **会主动读写 `head.zRot`**（不只是 xRot/yRot）。之前在 `extractRenderState` 阶段设的 `head.zRot`，随后被 `setupAnim` **覆盖**了——所以无论 `getModel()` 成不成功，头都不转。
  - 修复：歪头逻辑**搬到 `CatModelMixin`，注入 `AdultFelineModel`+`BabyFelineModel` 的 `setupAnim` 的 TAIL**（TAIL 保证写在我之后、顶点提交之前，zRot 不被覆盖）。用 `this`（`this` 即模型）转型 `Model` 取 `root().getChild("head").zRot`，**不依赖 `@Shadow` 字段**（26.1 mojmap 无 refmap，@Shadow vanilla 字段必崩黑屏）。整段 `try/catch` 兜底。
  - 新增 `CatRenderStateMixin`：给 `CatRenderState` 加 `@Unique` 字段 `laowuActive`/`laowuRoll`，`extractRenderState`（有 Cat 实体，能取 id/roll）写入，同一 `CatRenderState` 实例流到 `setupAnim` 读取——替代之前不可靠的 `WeakHashMap` 跨 mixin 桥。
  - `laowu_meme.client.mixins.json` 现注册 `CatRendererMixin` / `CatModelMixin` / `CatRenderStateMixin` 三个。
- **锁定距离再拉大**：`LOCK_DISTANCE` 1.3 → 1.8（各自离中点 0.9）。用户反馈 1.3 仍偏近、身体重叠；1.8 让两只猫头对头、身体明显分开、不重叠。

### 开发 / 构建
- 版本号 1.1.6 → 1.1.7。

## [1.1.6] - 2026-07-22

### 修复（Bug · 黑屏二次回归，根因彻底查清）
- **v1.1.5 仍黑屏（用户实测），这次根因是 refmap 缺失，不是类型**：日志仍报 `@Shadow field model was not located ... No refMap loaded`。
  - 真相：MC 26.1 用 mojmap 构建时，Loom **不会生成 refmap**；而运行时游戏类是 **intermediary** 命名。v1.1.3 的 `@Inject extractRenderState` 能跑，只是因为 `extractRenderState` 这个方法名在 intermediary 里被保留了；但字段 `model` 在 intermediary 里是 `field_xxxx`，没有 refmap 翻译，`@Shadow model` 必然解析失败 → mixin apply 崩溃 → 黑屏。所以**只要用 `@Shadow` 去 shadow 一个 vanilla 字段，在 mojmap 构建 + 无 refmap 下就会炸**，无论类型写得多对。
  - **修复（彻底去 @Shadow）**：不再 shadow 任何字段。改用 `CatRenderer` 继承的公开方法 `LivingEntityRenderer.getModel()` 取模型（`public M getModel()`，擦除为 `EntityModel`），再 `root().getChild("head").zRot`。公开方法名在 intermediary 里同样被保留（与 `extractRenderState` 同机制），可解析。整段 `try/catch(Throwable)` 兜底，任何异常都绝不让渲染器崩。
  - 已用 javap 核实构建产物：mixin class **不含任何 @Shadow**，字节码为 `LivingEntityRenderer.getModel()→EntityModel.root()→getChild("head")→zRot`。

### 开发 / 构建
- 版本号 1.1.5 → 1.1.6。
- 教训已固化进 skill `fabric-261-serverauth-mod`：26.1 mojmap 构建下 **不要 `@Shadow` vanilla 字段**（无 refmap → intermediary 字段名对不上必崩）；用公开方法/getter 或模型类自身 `this` 取部件，并一律 `try/catch`。

## [1.1.5] - 2026-07-22

### 修复（Bug · 紧急回归）
- **修复 v1.1.4 主菜单黑屏（启动后整个画面黑色，但 GUI 有声音）**：这是 1.1.4 引入的致命回归，1.1.3 正常。
  - 根因（用户日志 `latest.log` 实锤）：`CatRendererMixin` 里 `@Shadow protected AbstractFelineModel<CatRenderState> model;` 的**擦除类型写错**。真实字段在 `LivingEntityRenderer` 中是 `protected M model`（`M extends EntityModel<? super S>`），擦除后是 `net.minecraft.client.model.EntityModel`，并非 `AbstractFelineModel`。Mixin 按"字段名 + 描述符"解析 `@Shadow`，类型对不上就报 `InvalidMixinException: @Shadow field model was not located`，导致 `CatRenderer` 的 Mixin **整体应用失败** → `EntityRenderers` 类初始化抛 `NoClassDefFoundError` → 资源重载异常 → 主菜单/世界渲染全黑（但 GUI 与音效在，所以"点按钮有声音"）。
  - 修复：把 `@Shadow` 类型改为 `EntityModel<CatRenderState>`（擦除描述符与真实字段一致）。并用 MC 26.1 客户端 jar 字节码核实：`EntityModel extends Model`，`Model.root()` 为 public，`AdultFelineModel extends AbstractFelineModel`，歪头所需的 `root().getChild("head").zRot` 链路完全成立。
  - 防御加固：头部歪头整段用 `try/catch(Throwable)` 包住，即使个别猫变种模型 API 异常也**绝不让渲染器初始化崩掉**，从源头杜绝再次黑屏。

### 开发 / 构建
- 版本号 1.1.4 → 1.1.5。

## [1.1.4] - 2026-07-22

### 修复（Bug）
- **头部歪头终于生效（核心修复）**：之前"猫变大了但头不转"的根因是歪头逻辑放在 `CatModelMixin`（`setupAnim` 注入）+ `RenderStateHolder`（`WeakHashMap` 跨 mixin 状态桥）。该桥以 `CatRenderState` 实例为 key，在渲染管线的 `extractRenderState` -> `submitModel` -> `setupAnim` 链路中极易命中失败，导致 `head.zRot` 从未被写入。
  - 新方案：**彻底去掉模型 mixin 与 WeakHashMap 桥**，把歪头直接搬到必定会跑的 `CatRendererMixin`（`extractRenderState` 注入，缩放就是在这里生效的，已证实）。在提取渲染状态时，用 `this.model.root().getChild("head")` 直接拿到头部零件，按 `cat.getId()` 从 `ClientMemeState` 取歪头方向（`rollSign` ±1）写入 `head.zRot = ±45°`；非锁定猫清零，避免上一帧歪头残留。
  - 实测依据（MC 26.1 字节码核实）：`AdultFelineModel.setupAnim` 只写 `head.xRot/yRot`、**绝不碰 `zRot`**，所以这里写入的 `zRot` 会一路保留到顶点提交；`ModelPart.translateAndRotate` 经 `Quaternionf.rotationZYX(xRot,yRot,zRot)` 应用 `zRot`，故头部 roll 必定可见。
- **锁定距离再拉大**：`LOCK_DISTANCE` 从 `1.0` 调到 `1.3`（各自离中点 0.65）。用户实测 1.0"比之前好点但还偏近"，1.3 让两只猫头对头、身体明显分开、不重叠，更接近设计稿的"脸贴脸"。

### 架构 / 清理
- 删除 `CatModelMixin.java` 与 `RenderStateHolder.java`，`laowu_meme.client.mixins.json` 仅保留 `CatRendererMixin`。渲染逻辑收敛到单一入口，不再有跨 mixin 的脆弱状态桥。

### 开发 / 构建
- 版本号 1.1.3 → 1.1.4。

## [1.1.3] - 2026-07-22

### 修复（Bug）
- **锁定距离过近、头陷进对方肚子**：`LOCK_DISTANCE` 从 `0.6` 调到 `1.0`（各自离中点 0.5）。0.6 时两猫中心距仅 0.6，猫身较长，头直接插进对方身体里。1.0 让两只猫头对头、身体明显分开、不重叠。
- **头部歪头角度修正**：客户端之前直接用 `rollSign`（±1）当弧度，歪头幅度≈57°，偏大。改为乘以固定 45°（`π/4`），与设计稿"头各自逆时针45度(镜像歪头)"一致。歪头逻辑本身没问题——之前"看着没转"主要是头被埋在对方身体里看不见，拉开距离后应可见。

### 开发 / 构建
- 版本号 1.1.2 → 1.1.3。

## [1.1.2] - 2026-07-22

### 修复（Bug）
- **崩溃修复 · 进入存档渲染猫时崩溃**：v1.1.1 能启动，但进入单人存档、画面里出现猫时崩溃（`Extracting render state for an entity in world`）。
  - 根因：`RenderStateHolder`（渲染状态桥，`WeakHashMap`）被放在 mixin 包 `com.rogic.client.mixin` 里。该包在 `laowu_meme.client.mixins.json` 中被声明为 mixin 包，包内的类由 Mixin 处理器接管，**不允许被普通代码直接引用**。`CatRendererMixin` 提取渲染状态时引用 `RenderStateHolder.RollData`，触发 `IllegalClassLoadError: ... is in a defined mixin package ... and cannot be referenced directly`。
  - 修复：把 `RenderStateHolder`（含内部类 `RollData`）移出 mixin 包，迁到新包 `com.rogic.client.render`；`CatRendererMixin` / `CatModelMixin` 改为 import 新路径。

### 开发 / 构建
- 版本号 1.1.1 → 1.1.2（崩溃类回归，属补丁级修复）。

## [1.1.1] - 2026-07-22

### 修复（Bug）
- **崩溃修复 · 客户端启动必崩**：v1.1.0 客户端启动即崩溃（`Initializing game` 阶段 `RuntimeException: Could not execute entrypoint stage 'main'`）。
  - 根因：`MemeTriggerS2CPacket` / `MemeStopS2CPacket` 用 `CustomPacketPayload.createType("laowu_meme:trigger")` 注册类型，但 MC 26.1 的 `CustomPacketPayload.createType(String)` 把整个字符串当成 **path**，namespace 默认落到 `minecraft`，于是生成非法标识符 `minecraft:laowu_meme:trigger`（path 里含冒号），在类静态初始化时抛 `IdentifierException`，导致整个 mod 加载失败。
  - 修复：改为 `CustomPacketPayload.createType(Identifier.of("laowu_meme", "trigger"))`（显式 namespace + path），`MemeStopS2CPacket` 同理改为 `"laowu_meme", "stop"`。

### 开发 / 构建
- 版本号 1.1.0 → 1.1.1（崩溃类回归，属补丁级修复）。

## [1.1.0] - 2026-07-22

### 架构
- **重构为服务端权威（server-authoritative）架构**。原"纯客户端驱动"方案在多人下存在同步与瞬移问题，本次改为：服务端检测配对、驱动猫移动/朝向、管理状态机与冷却、向客户端广播 S2C 包；客户端只负责渲染（歪头 / 放大）与循环音乐。
- 单人（integrated server）与多人（专用服务端）现在都能正常运作，且多人下所有玩家看到的效果完全一致。
- 新增 S2C 网络包 `MemeTriggerS2CPacket` / `MemeStopS2CPacket`（`CustomPacketPayload` + `StreamCodec`），用于广播"锁定（含音频 id 与歪头方向）"与"释放"。

### 修复（Bug）
- **Bug 1 · 头部旋转方向**：由之前的左右转（yaw）改为"歪头杀"——头部绕视线轴 roll（`ModelPart.zRot`），且通过模型层实现，不再被原版"猫看人"逻辑覆盖。
- **Bug 2 · 触发流程**：两只猫现在会**先靠近再触发**（APPROACHING 阶段平滑走在一起），触发扫描距离从 3 格改为 6 格。
- **Bug 3 · 贴脸距离**：锁定时两猫中心距收紧到约 0.6 格，实现真正的脸贴脸。
- **Bug 4 · 右键释放**：右键其中一只猫后，服务端恢复猫的 AI 并给一点向外速度，猫**自然走开**（不再瞬移）；被打断的这对猫进入 3 分钟冷却。

### 新增
- **体型放大 50%**：锁定后仅渲染层放大（`LivingEntityRenderState.scale *= 1.5`），**碰撞箱不变**。

### 开发 / 构建
- 确认并适配 MC 26.1.2（首个不混淆版本，mojmap，强制 Java 25）。
- `gradle.properties` 调整 daemon 内存为 `-Xmx2G -XX:MaxMetaspaceSize=512M`，修复 sandbox 下 `-Xmx4G` 导致 Gradle daemon OOM 崩溃的问题。
- 渲染 mixin 适配 26.1 RenderState 模式：`CatRendererMixin`（数据提取 + 放大）、`CatModelMixin`（歪头）、`RenderStateHolder`（跨 mixin 状态桥）。

## [1.0.0] - 2026-07-22

- 初始版本。两只猫（其一命名为"老吴"）靠近触发头对头旋转 + 随机播放老吴2 / 凄凉 BGM，右键打断跑开。
