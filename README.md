# Systemd Web 管理平台

## 项目简介

Systemd Web 是一个基于Web的systemd服务管理平台，提供图形化界面来管理系统中的服务单元。用户可以通过浏览器方便地查看、启动、停止、重启系统服务，以及上传自定义服务配置文件。

## 功能特性

- 系统级和用户级服务管理
- 服务状态实时监控
- 服务启停、重启、启用、禁用等操作
- 服务文件上传和模板生成
- 搜索和筛选功能
- 分页显示服务列表
- 响应式Web界面

## 技术架构

### 前端技术栈

- Vue 3 + TypeScript
- Element Plus UI 组件库
- Vite 构建工具
- Axios HTTP客户端
- Vue Router 路由管理

### 后端技术栈

- Spring Boot 4.0.3
- Java 25
- Maven 构建工具
- GraalVM Native Image 支持

## 环境要求

### 前端环境

- Node.js >= 20.19.0 或 >= 22.12.0
- pnpm 包管理器

### 后端环境

- Java 25
- Maven 3.6+
- systemd 系统

## 快速开始

### 前端部署

1. 进入前端目录

```bash
cd frontend
```

2. 安装依赖

```bash
pnpm install
```

3. 开发模式运行

```bash
pnpm dev
```

4. 生产构建

```bash
pnpm build
```

### 后端部署

1. 进入后端目录

```bash
cd backend
```

2. 编译项目

```bash
./mvnw clean compile
```

3. 运行应用

```bash
./mvnw spring-boot:run
```

4. 构建Native Image（可选）

```bash
./mvnw native:compile -Pnative
```

## 使用说明

### 基本操作流程

1. 访问Web界面，默认端口为前端开发服务器端口
2. 选择运行级别（系统级或用户级）
3. 查看服务列表及其状态
4. 通过操作按钮对服务进行管理
5. 可使用搜索功能快速定位特定服务
6. 支持上传自定义服务配置文件

### 权限说明

由于涉及系统服务管理，建议：

- 在生产环境中使用HTTPS
- 配置适当的身份验证机制
- 限制访问IP范围
- 使用非root用户运行应用

## 故障排除

### 常见问题

1. **权限不足**
   - 确保运行用户具有systemctl命令执行权限
   - 检查systemd服务目录读取权限

2. **构建失败**
   - 检查Node.js和Java版本是否符合要求
   - 清理缓存重新安装依赖
   - 查看详细错误日志

## 安全注意事项

- 不要在公网直接暴露此管理界面
- 建议部署在内网环境中
- 定期更新依赖包修复安全漏洞
- 实施适当的访问控制策略

## 贡献指南

欢迎提交Issue和Pull Request来改进项目：

1. Fork项目仓库
2. 创建功能分支
3. 提交更改
4. 发起Pull Request

## 许可证

本项目采用MIT许可证，详见LICENSE文件。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 提交GitHub Issue
- 发送邮件至项目维护者
