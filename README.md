taskflow example
=========

## 简介
   TaskFlow 是一个工作流/任务流的编排引擎系统，旨在简化复杂任务的管理和调度，同时提供了开放API平台接入Sdk能力，使开发者能够轻松注册工作任务节点到云平台，
   通过图形化界面拖拽方式实现工作流创建，也可以直接使用Sdk API直接来创建和管理工作流。

### 功能   
* 自定义工作任务&发布
* 工作流注册&发布
* 工作流运行&管理
* 工作流触发器配置和管理

### 工程模块
```plaintext
   +-- taskflow-sample
   |   +-- common (公共子模块)
   |   +-- java-native-sample (演示Java原生工程集成sdk)
   |   +-- spring-worker-sample (演示Spring工程的形式定义 worker 示例)
   |   +-- spring-workflow-sample (演示Spring工程的形式定义 workflow)
   |   +-- trigger-sample  (演示工作流触发定义和管理)

```

### 运行项目

克隆仓库:
```bash
 git clone https://github.com/kevinLuan/taskflow-sample.git
 cd taskflow-sample
```

### 获取 keyId 和 secret 方法
访问官网 [任务云](http://www.taskflow.cn) 注册并登录账号，点击应用管理，创建开发者应用即可获取 keyId 和 secret。
