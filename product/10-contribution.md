---
order: 10
title: '10.开源共建'
toc: menu
---

## 10.1 贡献源码

欢迎 👏🏻 来到 KnowStreaming！本文档是关于如何为 KnowStreaming 做出贡献的指南。

如果您发现不正确或遗漏的内容, 请留下意见/建议。

#### 行为守则

请务必阅读并遵守我们的 [行为准则](https://github.com/didi/KnowStreaming/blob/master/CODE_OF_CONDUCT.md).

#### 贡献

**KnowStreaming** 欢迎任何角色的新参与者，包括 **User** 、**Contributor**、**Committer**、**PMC** 。

我们鼓励新人积极加入 **KnowStreaming** 项目，从 User 到 Contributor、Committer ，甚至是 PMC 角色。

为了做到这一点，新人需要积极地为 **KnowStreaming** 项目做出贡献。以下介绍如何对 **KnowStreaming** 进行贡献。

##### 创建/打开 Issue

如果您在文档中发现拼写错误、在代码中**发现错误**或想要**新功能**或想要**提供建议**，您可以在 GitHub 上[创建一个 Issue](https://github.com/didi/KnowStreaming/issues/new/choose) 进行报告。

如果您想直接贡献, 您可以选择下面标签的问题。

- [contribution welcome](https://github.com/didi/KnowStreaming/labels/contribution%20welcome) : 非常需要解决/新增 的 Issues
- [good first issue](https://github.com/didi/KnowStreaming/labels/good%20first%20issue): 对新人比较友好, 新人可以拿这个 Issue 来练练手热热身。

<font color=red ><b> 请注意，任何 PR 都必须与有效 issue 相关联。否则，PR 将被拒绝。</b></font>

##### 开始你的贡献

**分支介绍**

我们将 `master`分支作为开发分支, 说明这是一个不稳定的分支。

此外,我们的分支模型符合 [https://nvie.com/posts/a-successful-git-branching-model/](https://nvie.com/posts/a-successful-git-branching-model/). 我们强烈建议新人在创建 PR 之前先阅读上述文章。

**贡献流程**

为方便描述,我们这里定义一下 2 个名词：

自己 Fork 出来的仓库是私人仓库, 我们这里称之为 ：**分叉仓库**
Fork 的源项目,我们称之为：**源仓库**

现在，如果您准备好创建 PR, 以下是贡献者的工作流程:

1. Fork [KnowStreaming](https://github.com/didi/KnowStreaming) 项目到自己的仓库

2. 从源仓库的`master`拉取并创建自己的本地分支,例如: `master`
3. 在本地分支上对代码进行修改
4. Rebase 开发分支, 并解决冲突
5. commit 并 push 您的更改到您自己的**分叉仓库**
6. 创建一个 Pull Request 到**源仓库**的`master`分支中。
7. 等待回复。如果回复的慢，请无情的催促。

更为详细的贡献流程请看下面的：[10.2 贡献流程]()

创建 Pull Request 时：

1. 请遵循 PR 的 [模板](https://github.com/didi/KnowStreaming/blob/master/.github/PULL_REQUEST_TEMPLATE.md)
2. 请确保 PR 有相应的 issue。
3. 如果您的 PR 包含较大的更改，例如组件重构或新组件，请编写有关其设计和使用的详细文档(在对应的 issue 中)。
4. 注意单个 PR 不能太大。如果需要进行大量更改，最好将更改分成几个单独的 PR。
5. 在合并 PR 之前，尽量的将最终的提交信息清晰简洁, 将多次修改的提交尽可能的合并为一次提交。
6. 创建 PR 后，将为 PR 分配一个或多个 reviewers。

<font color=red><b>如果您的 PR 包含较大的更改，例如组件重构或新组件，请编写有关其设计和使用的详细文档。</b></font>

#### 代码审查指南

Commiter 将轮流 review 代码，以确保在合并前至少有一名 Commiter

一些原则：

- 可读性——重要的代码应该有详细的文档。API 应该有 Javadoc。代码风格应与现有风格保持一致。
- 优雅：新的函数、类或组件应该设计得很好。
- 可测试性——单元测试用例应该覆盖 80% 的新代码。
- 可维护性 - 遵守我们的编码规范。

#### 开发者

##### 成为 Contributor

只要成功提交并合并 PR , 则为 Contributor

贡献者名单请看：[贡献者名单]()

##### 尝试成为 Commiter

一般来说, 贡献 8 个重要的补丁并至少让三个不同的人来 Review 他们(您需要 3 个 Commiter 的支持)。
然后请人给你提名, 您需要展示您的

1. 至少 8 个重要的 PR 和项目的相关问题
2. 与团队合作的能力
3. 了解项目的代码库和编码风格
4. 编写好代码的能力

当前的 Commiter 可以通过在 KnowStreaming 中的 Issue 标签 `nomination`(提名)来提名您

1. 你的名字和姓氏
2. 指向您的 Git 个人资料的链接
3. 解释为什么你应该成为 Commiter
4. 详细说明提名人与您合作的 3 个 PR 以及相关问题,这些问题可以证明您的能力。

另外 2 个 Commiter 需要支持您的**提名**，如果 5 个工作日内没有人反对，您就是提交者,如果有人反对或者想要更多的信息，Commiter 会讨论并通常达成共识(5 个工作日内) 。

<br/>
<br/>
<br/>
<br/>

<hr>

<br/>
<br/>
<br/>
<br/>

## 10.2 贡献流程

#### 贡献流程

[贡献源码细则](./././CONTRIBUTING.md)

##### 1. fork didi/KnowStreaming 项目到您的 github 库

找到你要 Fork 的项目,例如 [KnowStreaming](https://github.com/didi/KnowStreaming) ,点击 Fork 按钮。

![在这里插入图片描述](https://img-blog.csdnimg.cn/ac7bfef9ccde49d587c30e702a615ef5.png)

##### 2. 克隆或下载您 fork 的 Know Streaming 代码仓库到您本地

```sh

git clone { your fork knowstreaming repo address }

cd KnowStreaming

```

##### 3. 添加 didi/KnowStreaming 仓库为 upstream 仓库

```sh

### 添加源仓库
git remote add upstream https://github.com/didi/KnowStreaming

### 查看是否添加成功
git remote -v

    origin	   ${your fork KnowStreaming repo address} (fetch)
    origin	   ${your fork KnowStreaming repo address} (push)
    upstream	https://github.com/didi/KnowStreaming(fetch)
    upstream	https://github.com/didi/KnowStreaming (push)

### 获取源仓库的基本信息
git fetch origin
git fetch upstream

```

上面是将 didi/KnowStreaming 添加为远程仓库, 当前就会有 2 个远程仓库

1. origin ： 你 Fork 出来的分叉仓库
2. upstream ： 源仓库

git fetch 获取远程仓库的基本信息, 比如 **源仓库**的所有分支就获取到了

##### 4. 同步源仓库开发分支到本地分叉仓库中

一般开源项目都会有一个给贡献者提交代码的分支，例如 KnowStreaming 的分支是 `master`；

首先我们要将 **源仓库**的开发分支(`master`) 拉取到本地仓库中

```sh

git checkout -b master upstream/master
```

**或者 IDEA 的形式创建**

![在这里插入图片描述](https://img-blog.csdnimg.cn/c95f2601a9af41889a5fc20b2a9724a5.png)

##### 5. 在本地新建的开发分支上进行修改

首先请保证您阅读并正确设置 KnowStreaming code style, 相关内容请阅读[KnowStreaming 代码规约 ]()。

修改时请保证该分支上的修改仅和 issue 相关，并尽量细化，做到

<font color=red><b>一个分支只修改一件事，一个 PR 只修改一件事</b></font>。

同时，您的提交记录请尽量描述清楚，主要以谓 + 宾进行描述，如：Fix xxx problem/bug。少量简单的提交可以使用 For xxx 来描述，如：For codestyle。 如果该提交和某个 ISSUE 相关，可以添加 ISSUE 号作为前缀，如：For #10000, Fix xxx problem/bug。

##### 6. Rebase 基础分支和开发分支

您修改的时候，可能别人的修改已经提交并被合并，此时可能会有冲突，这里请使用 rebase 命令进行合并解决，主要有 2 个好处：

1. 您的提交记录将会非常优雅，不会出现 Merge xxxx branch 等字样
2. rebase 后您分支的提交日志也是一条单链，基本不会出现各种分支交错的情况，回查时更轻松

```sh
git fetch upstream

git rebase -i upstream/master

```

**或者在 IDEA 的操作如下**

![在这里插入图片描述](https://img-blog.csdnimg.cn/d75addcfa9564d3d9e1d226a2f7f4d64.png)

选择 源仓库的开发分支

![在这里插入图片描述](https://img-blog.csdnimg.cn/4e85714df13b44bcb10f1e655450cb72.png)

推荐使用 IDEA 的方式, 有冲突的时候更容易解决冲突问题。

##### 7. 将您开发完成 rebase 后的分支，上传到您 fork 的仓库

```sh
git push origin master
```

特别要注意的是：在 push 之前 尽量将您的多次 commit 信息 合并成一次 commit 信息，这样会非常的简洁

##### 8. 按照 PR 模板中的清单创建 Pull Request

![在这里插入图片描述](https://img-blog.csdnimg.cn/1dab060aed314666970e3910e05f2205.png)

选择自己的分支合并到模板分支。

##### 9. 等待合并代码

提交了 PR 之后，需要等待 PMC、Commiter 来 Review 代码，如果有问题需要配合修改重新提交。

如果没有问题会直接合并到开发分支`master`中。

注： 如果长时间没有 review, 则可以多催促社区来 Review 代码！

<br/>
<br/>
<br/>
<br/>

<hr>

<br/>
<br/>
<br/>
<br/>

## 10.3 Pull Request 模板

请不要在没有先创建 Issue 的情况下创建 Pull Request。

#### 变更的目的是什么

XXXXX

#### 简短的更新日志

XX

#### 验证这一变化

XXXX

请遵循此清单，以帮助我们快速轻松地整合您的贡献：

- [ ] 确保有针对更改提交的 Github issue（通常在您开始处理之前）。诸如拼写错误之类的琐碎更改不需要 Github issue。您的 Pull Request 应该只解决这个问题，而不需要进行其他更改—— 一个 PR 解决一个问题。
- [ ] 格式化 Pull Request 标题，如[ISSUE #123] support Confluent Schema Registry。 Pull Request 中的每个提交都应该有一个有意义的主题行和正文。
- [ ] 编写足够详细的 Pull Request 描述，以了解 Pull Request 的作用、方式和原因。
- [ ] 编写必要的单元测试来验证您的逻辑更正。如果提交了新功能或重大更改，请记住在 test 模块中添加 integration-test
- [ ] 确保编译通过，集成测试通过

<br/>
<br/>
<br/>
<br/>

<hr>

<br/>
<br/>
<br/>
<br/>

## 10.4 如果提交问题报告

在[提交问题](https://github.com/didi/KnowStreaming/issues/new/choose)的时候,请选择合适的模板来创建。

按照每个类型的问题模板描述清楚！

<br/>
<br/>
<br/>
<br/>

<hr>

<br/>
<br/>
<br/>
<br/>

## 10.5 开源激励计划

我们非常欢迎开发者们为 KnowStreaming 开源项目贡献一份力量，相应也将给予贡献者激励以表认可与感谢。

#### 参与贡献

1. 积极参与 Issue 的讨论，如答疑解惑、提供想法或报告无法解决的错误（Issue）
2. 撰写和改进项目的文档（Wiki）
3. 提交补丁优化代码（Coding）

#### 你将获得

1. 加入 KnowStreaming 开源项目贡献者名单并展示
2. KnowStreaming 开源贡献者证书(纸质&电子版)
3. KnowStreaming 贡献者精美大礼包(KnowStreamin/滴滴 周边)

#### 相关规则

- Contributer 和 Commiter 都会有对应的证书和对应的礼包
- 每季度有 KnowStreaming 项目团队评选出杰出贡献者,颁发相应证书。
- 年末进行年度评选

<br/>
<br/>
<br/>
<br/>

<hr>

<br/>
<br/>
<br/>
<br/>

## 10.6 贡献者名单

#### KnowStreaming 开发者角色

KnowStreaming 开发者包含 Maintainer、Committer、Contributor 三种角色，每种角色的标准定义如下。

##### Maintainer

Maintainer 是对 KnowStreaming 项目的演进和发展做出显著贡献的个人。具体包含以下的标准：

- 完成多个关键模块或者工程的设计与开发，是项目的核心开发人员；
- 持续的投入和激情，能够积极参与社区、官网、issue、PR 等项目相关事项的维护；
- 在社区中具有有目共睹的影响力，能够代表 KnowStreaming 参加重要的社区会议和活动；
- 具有培养 Committer 和 Contributor 的意识和能力；

##### Committer

Committer 是具有 KnowStreaming 仓库写权限的个人，包含以下的标准：

- 能够在长时间内做持续贡献 issue、PR 的个人；
- 参与 issue 列表的维护及重要 feature 的讨论；
- 参与 code review；

##### Contributor

Contributor 是对 KnowStreaming 项目有贡献的个人，标准为：

- 提交过 PR 并被合并；

开源贡献者名单(定期更新)

在名单内,但是没有收到贡献者礼品的同学,可以联系：szzdzhp001

| 姓名                | Github                                                     | 角色        | 公司     |
| ------------------- | ---------------------------------------------------------- | ----------- | -------- |
| 张亮                | [@zhangliangboy](https://github.com/zhangliangboy)         | Maintainer  | 滴滴出行 |
| 谢鹏                | [@PenceXie](https://github.com/PenceXie)                   | Maintainer  | 滴滴出行 |
| 赵情融              | [@zqrferrari](https://github.com/zqrferrari)               | Maintainer  | 滴滴出行 |
| 石臻臻              | [@shirenchuang](https://github.com/shirenchuang)           | Maintainer  | 滴滴出行 |
| 曾巧                | [@ZQKC](https://github.com/ZQKC)                           | Maintainer  | 滴滴出行 |
| 孙超                | [@lucasun](https://github.com/lucasun)                     | Maintainer  | 滴滴出行 |
| 洪华驰              | [@brodiehong](https://github.com/brodiehong)               | Maintainer  | 滴滴出行 |
| 许喆                | [@potaaaaaato](https://github.com/potaaaaaato)             | Committer   | 滴滴出行 |
| 郭宇航              | [@GraceWalk](https://github.com/GraceWalk)                 | Committer   | 滴滴出行 |
| 李伟                | [@velee](https://github.com/velee)                         | Committer   | 滴滴出行 |
| 张占昌              | [@zzccctv](https://github.com/zzccctv)                     | Committer   | 滴滴出行 |
| 王东方              | [@wangdongfang-aden](https://github.com/wangdongfang-aden) | Committer   | 滴滴出行 |
| 王耀波              | [@WYAOBO](https://github.com/WYAOBO)                       | Committer   | 滴滴出行 |
| 赵寅锐              | [@ZHAOYINRUI](https://github.com/ZHAOYINRUI)               | Maintainer  | 字节跳动 |
| haoqi123            | [@haoqi123](https://github.com/haoqi123)                   | Contributor | 前程无忧 |
| chaixiaoxue         | [@chaixiaoxue](https://github.com/chaixiaoxue)             | Contributor | SYNNEX   |
| 陆晗                | [@luhea](https://github.com/luhea)                         | Contributor | 竞技世界 |
| Mengqi777           | [@Mengqi777](https://github.com/Mengqi777)                 | Contributor | 腾讯     |
| ruanliang-hualun    | [@ruanliang-hualun](https://github.com/ruanliang-hualun)   | Contributor | 网易     |
| 17hao               | [@17hao](https://github.com/17hao)                         | Contributor |          |
| Huyueeer            | [@Huyueeer](https://github.com/Huyueeer)                   | Contributor | INVENTEC |
| lomodays207         | [@lomodays207](https://github.com/lomodays207)             | Contributor | 建信金科 |
| Super .Wein（星痕） | [@superspeedone](https://github.com/superspeedone)         | Contributor | 韵达     |
| Hongten             | [@Hongten](https://github.com/Hongten)                     | Contributor | Shopee   |
| 徐正熙              | [@hyper-xx)](https://github.com/hyper-xx)                  | Contributor | 滴滴出行 |
| RichardZhengkay     | [@RichardZhengkay](https://github.com/RichardZhengkay)     | Contributor | 趣街     |
| 罐子里的茶          | [@gzldc](https://github.com/gzldc)                         | Contributor | 道富     |
| 陈忠玉              | [@paula](https://github.com/chenzhongyu11)                 | Contributor | 平安产险 |
| 杨光                | [@yaangvipguang](https://github.com/yangvipguang)          | Contributor |
| 王亚聪              | [@wangyacongi](https://github.com/wangyacongi)             | Contributor |
| Yang Jing           | [@yangbajing](https://github.com/yangbajing)               | Contributor |          |
| 刘新元 Liu XinYuan  | [@Liu-XinYuan](https://github.com/Liu-XinYuan)             | Contributor |          |
| Joker               | [@LiubeyJokerQueue](https://github.com/JokerQueue)         | Contributor | 丰巢     |
| Eason Lau           | [@Liubey](https://github.com/Liubey)                       | Contributor |          |
| hailanxin           | [@hailanxin](https://github.com/hailanxin)                 | Contributor |          |
| Qi Zhang            | [@zzzhangqi](https://github.com/zzzhangqi)                 | Contributor | 好雨科技 |
| fengxsong           | [@fengxsong](https://github.com/fengxsong)                 | Contributor |          |
| 谢晓东              | [@Strangevy](https://github.com/Strangevy)                 | Contributor | 花生日记 |
| ZhaoXinlong         | [@ZhaoXinlong](https://github.com/ZhaoXinlong)             | Contributor |          |
| xuehaipeng          | [@xuehaipeng](https://github.com/xuehaipeng)               | Contributor |          |
| 孔令续              | [@mrazkong](https://github.com/mrazkong)                   | Contributor |          |
| pierre xiong        | [@pierre94](https://github.com/pierre94)                   | Contributor |          |
| PengShuaixin        | [@PengShuaixin](https://github.com/PengShuaixin)           | Contributor |          |
| 梁壮                | [@lz](https://github.com/silent-night-no-trace)            | Contributor |          |
| 张晓寅              | [@ahu0605](https://github.com/ahu0605)                     | Contributor | 电信数智 |
| 黄海婷              | [@Huanghaiting](https://github.com/Huanghaiting)           | Contributor | 云徙科技 |
| 任祥德              | [@RenChauncy](https://github.com/RenChauncy)               | Contributor | 探马企服 |
