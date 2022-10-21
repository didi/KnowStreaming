


# 为KnowStreaming做贡献


欢迎👏🏻来到KnowStreaming！本文档是关于如何为KnowStreaming做出贡献的指南。

如果您发现不正确或遗漏的内容, 请留下意见/建议。

## 行为守则
请务必阅读并遵守我们的 [行为准则](./CODE_OF_CONDUCT.md).



## 贡献

**KnowStreaming** 欢迎任何角色的新参与者，包括 **User** 、**Contributor**、**Committer**、**PMC** 。

我们鼓励新人积极加入 **KnowStreaming** 项目，从User到Contributor、Committer ，甚至是 PMC 角色。

为了做到这一点，新人需要积极地为 **KnowStreaming** 项目做出贡献。以下介绍如何对 **KnowStreaming** 进行贡献。


### 创建/打开  Issue

如果您在文档中发现拼写错误、在代码中**发现错误**或想要**新功能**或想要**提供建议**，您可以在 GitHub 上[创建一个Issue](https://github.com/didi/KnowStreaming/issues/new/choose) 进行报告。


如果您想直接贡献, 您可以选择下面标签的问题。

- [contribution welcome](https://github.com/didi/KnowStreaming/labels/contribution%20welcome) : 非常需要解决/新增  的Issues
- [good first issue](https://github.com/didi/KnowStreaming/labels/good%20first%20issue): 对新人比较友好, 新人可以拿这个Issue来练练手热热身。

<font color=red ><b> 请注意，任何 PR 都必须与有效issue相关联。否则，PR 将被拒绝。</b></font>



### 开始你的贡献

**分支介绍**

我们将 `dev`分支作为开发分支, 说明这是一个不稳定的分支。

此外,我们的分支模型符合 [https://nvie.com/posts/a-successful-git-branching-model/](https://nvie.com/posts/a-successful-git-branching-model/). 我们强烈建议新人在创建PR之前先阅读上述文章。



**贡献流程**

为方便描述,我们这里定义一下2个名词：

自己Fork出来的仓库是私人仓库, 我们这里称之为 ：**分叉仓库**
Fork的源项目,我们称之为：**源仓库**


现在，如果您准备好创建PR, 以下是贡献者的工作流程:

1. Fork [KnowStreaming](https://github.com/didi/KnowStreaming) 项目到自己的仓库

2. 从源仓库的`dev`拉取并创建自己的本地分支,例如: `dev`
3. 在本地分支上对代码进行修改
4. Rebase 开发分支, 并解决冲突
5. commit 并 push 您的更改到您自己的**分叉仓库**
6. 创建一个 Pull Request 到**源仓库**的`dev`分支中。
7. 等待回复。如果回复的慢，请无情的催促。


更为详细的贡献流程请看：[贡献流程](./docs/contributer_guide/贡献流程.md)

创建Pull Request时：

1. 请遵循 PR的 [模板](./.github/PULL_REQUEST_TEMPLATE.md)
2. 请确保 PR 有相应的issue。
3. 如果您的 PR 包含较大的更改，例如组件重构或新组件，请编写有关其设计和使用的详细文档(在对应的issue中)。
4. 注意单个 PR 不能太大。如果需要进行大量更改，最好将更改分成几个单独的 PR。
5. 在合并PR之前，尽量的将最终的提交信息清晰简洁, 将多次修改的提交尽可能的合并为一次提交。
6. 创建 PR 后，将为PR分配一个或多个reviewers。


<font color=red><b>如果您的 PR 包含较大的更改，例如组件重构或新组件，请编写有关其设计和使用的详细文档。</b></font>


# 代码审查指南

Commiter将轮流review代码，以确保在合并前至少有一名Commiter

一些原则：

- 可读性——重要的代码应该有详细的文档。API 应该有 Javadoc。代码风格应与现有风格保持一致。
- 优雅：新的函数、类或组件应该设计得很好。
- 可测试性——单元测试用例应该覆盖 80% 的新代码。
- 可维护性 - 遵守我们的编码规范。


# 开发者

## 成为Contributor

只要成功提交并合并PR , 则为Contributor

贡献者名单请看：[贡献者名单](./docs/contributer_guide/开发者名单.md)

## 尝试成为Commiter

一般来说, 贡献8个重要的补丁并至少让三个不同的人来Review他们(您需要3个Commiter的支持)。
然后请人给你提名, 您需要展示您的

1. 至少8个重要的PR和项目的相关问题
2. 与团队合作的能力
3. 了解项目的代码库和编码风格
4. 编写好代码的能力

当前的Commiter可以通过在KnowStreaming中的Issue标签 `nomination`(提名)来提名您

1. 你的名字和姓氏
2. 指向您的Git个人资料的链接
3. 解释为什么你应该成为Commiter
4. 详细说明提名人与您合作的3个PR以及相关问题,这些问题可以证明您的能力。

另外2个Commiter需要支持您的**提名**，如果5个工作日内没有人反对，您就是提交者,如果有人反对或者想要更多的信息，Commiter会讨论并通常达成共识(5个工作日内) 。


# 开源奖励计划


我们非常欢迎开发者们为KnowStreaming开源项目贡献一份力量，相应也将给予贡献者激励以表认可与感谢。


## 参与贡献

1. 积极参与 Issue 的讨论，如答疑解惑、提供想法或报告无法解决的错误（Issue）
2. 撰写和改进项目的文档（Wiki）
3. 提交补丁优化代码（Coding）


## 你将获得

1. 加入KnowStreaming开源项目贡献者名单并展示
2. KnowStreaming开源贡献者证书(纸质&电子版)
3. KnowStreaming贡献者精美大礼包(KnowStreamin/滴滴  周边)


## 相关规则

- Contributer和Commiter都会有对应的证书和对应的礼包
- 每季度有KnowStreaming项目团队评选出杰出贡献者,颁发相应证书。
- 年末进行年度评选

贡献者名单请看：[贡献者名单](./docs/contributer_guide/开发者名单.md)