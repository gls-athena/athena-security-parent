# 贡献指南

感谢您对 Athena Security 项目的关注！我们非常欢迎社区的贡献，无论是代码、文档、测试还是其他形式的参与。

## 🎯 贡献方式

我们接受以下类型的贡献：

- 🐛 **Bug修复** - 发现并修复项目中的问题
- ✨ **新功能** - 提出并实现有价值的新特性
- 📚 **文档改进** - 完善项目文档、示例和教程
- 🧪 **测试增强** - 添加测试用例，提高代码覆盖率
- 🎨 **代码优化** - 改进代码质量、性能或结构
- 🌐 **国际化** - 添加多语言支持
- 💡 **想法建议** - 提出改进建议和功能需求

## 🚀 快速开始

### 1. 准备开发环境

确保您的开发环境满足以下要求：

- **JDK**: 17 或更高版本
- **Maven**: 3.6+
- **Git**: 最新版本
- **IDE**: IntelliJ IDEA 或 Eclipse（推荐 IntelliJ IDEA）
- **Redis**: 6.0+ （用于测试）
- **MySQL**: 8.0+ （用于测试）

### 2. Fork 并克隆项目

```bash
# 1. 在 GitHub 上 Fork 项目
# 2. 克隆您的 Fork 到本地
git clone https://github.com/YOUR_USERNAME/athena-security.git
cd athena-security

# 3. 添加上游仓库
git remote add upstream https://github.com/gls/athena-security.git

# 4. 验证远程仓库设置
git remote -v
```

### 3. 设置开发分支

```bash
# 确保主分支是最新的
git checkout main
git pull upstream main

# 创建新的功能分支
git checkout -b feature/your-feature-name
```

## 🛠️ 开发规范

### 代码风格

我们使用统一的代码风格来保持项目的一致性：

- **Java代码规范**: 遵循 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- **缩进**: 使用 4 个空格，不使用 Tab
- **行长度**: 最大 120 字符
- **编码**: UTF-8

### 命名规范

- **类名**: 使用 PascalCase，如 `UserAuthenticationService`
- **方法名**: 使用 camelCase，如 `authenticateUser()`
- **常量**: 使用 UPPER_SNAKE_CASE，如 `MAX_RETRY_COUNT`
- **包名**: 使用小写，如 `com.gls.athena.security.web`

### Git提交规范

我们使用 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

**类型 (type):**

- `feat`: 新功能
- `fix`: Bug修复
- `docs`: 文档更新
- `style`: 代码格式化（不影响功能）
- `refactor`: 代码重构
- `test`: 添加或修改测试
- `chore`: 构建过程或辅助工具变动

**示例:**

```bash
feat(oauth2): 添加飞书OAuth2客户端支持

- 实现飞书OAuth2认证流程
- 添加用户信息获取接口
- 完善相关配置选项

Closes #123
```

## 🧪 测试要求

### 单元测试

- 新增功能必须包含相应的单元测试
- 测试覆盖率应保持在 80% 以上
- 使用 JUnit 5 和 Mockito 进行测试

```bash
# 运行所有测试
mvn test

# 运行特定模块测试
mvn test -pl athena-security-web

# 生成测试覆盖率报告
mvn jacoco:report
```

### 集成测试

- 对于复杂功能，需要编写集成测试
- 使用 `@SpringBootTest` 进行 Spring 上下文测试
- 使用 Testcontainers 进行数据库和 Redis 测试

## 📝 文档要求

### 代码文档

- 所有公共 API 必须有 Javadoc 注释
- 复杂逻辑需要添加行内注释
- 新增功能需要更新相关文档

```java
/**
 * 用户认证服务接口
 *
 * 提供用户登录、注销等认证相关功能
 *
 * @author your-name
 * @since 0.0.1
 */
public interface UserAuthenticationService {

    /**
     * 用户登录认证
     *
     * @param username 用户名
     * @param password 密码
     * @return 认证结果
     * @throws AuthenticationException 认证失败异常
     */
    AuthenticationResult authenticate(String username, String password);
}
```

### 更新文档

如果您的更改影响了以下内容，请相应更新文档：

- API 接口变更 → 更新 API 文档
- 配置选项变更 → 更新配置文档
- 新增功能 → 更新 README 和用户指南
- 依赖变更 → 更新安装指南

## 🔄 提交流程

### 1. 提交前检查

```bash
# 代码格式检查
mvn spotless:check

# 运行所有测试
mvn clean test

# 编译检查
mvn clean compile
```

### 2. 提交代码

```bash
# 添加文件到暂存区
git add .

# 提交代码（遵循提交规范）
git commit -m "feat(oauth2): 添加微信OAuth2客户端支持"

# 推送到您的 Fork
git push origin feature/your-feature-name
```

### 3. 创建 Pull Request

1. 在 GitHub 上访问您的 Fork
2. 点击 "New Pull Request"
3. 填写 PR 标题和描述：

```markdown
## 📋 变更类型

- [ ] Bug 修复
- [x] 新功能
- [ ] 文档更新
- [ ] 性能优化
- [ ] 其他

## 📝 变更描述

添加微信OAuth2客户端支持，包括：

- 微信授权登录流程
- 用户信息获取
- 相关配置选项

## 🧪 测试情况

- [x] 单元测试通过
- [x] 集成测试通过
- [x] 手动测试通过

## 📚 相关文档

- 更新了 OAuth2 配置文档
- 添加了微信登录示例

## ✅ 检查清单

- [x] 代码符合项目规范
- [x] 添加了必要的测试
- [x] 更新了相关文档
- [x] 提交信息符合规范
```

## 📋 Review 流程

### Reviewer 指南

- 检查代码质量和规范性
- 验证功能是否符合预期
- 确认测试覆盖率充足
- 检查文档是否完整

### 常见问题

**Q: 如何解决合并冲突？**

```bash
# 获取最新的上游代码
git fetch upstream
git checkout main
git merge upstream/main

# 切换到功能分支并合并
git checkout feature/your-feature-name
git merge main

# 解决冲突后提交
git add .
git commit -m "resolve merge conflicts"
git push origin feature/your-feature-name
```

**Q: 如何更新已提交的 PR？**

```bash
# 在功能分支上进行修改
git add .
git commit -m "fix: 修复 review 意见"
git push origin feature/your-feature-name
```

## 🏷️ 发布流程

### 版本号规范

我们使用 [Semantic Versioning](https://semver.org/)：

- **MAJOR**: 不兼容的 API 变更
- **MINOR**: 向后兼容的功能新增
- **PATCH**: 向后兼容的问题修复

### Release Notes

每个版本都需要详细的发布说明：

```markdown
## [0.2.0] - 2025-01-15

### Added

- 新增微信OAuth2客户端支持
- 添加验证码防刷功能

### Changed

- 优化Session管理性能
- 更新Spring Security到6.2版本

### Fixed

- 修复OAuth2令牌刷新问题
- 解决并发登录异常
```

## 🎖️ 贡献者奖励

我们重视每一个贡献者的付出：

- **代码贡献者**: 会在 Contributors 列表中展示
- **文档贡献者**: 在文档中署名感谢
- **重要贡献者**: 邀请成为项目维护者
- **社区活跃者**: 特殊徽章和称号

## 💬 社区交流

- **GitHub Issues**: 报告问题和功能请求
- **GitHub Discussions**: 技术讨论和问答
- **邮件列表**: athena-security-dev@gls.com
- **微信群**: 扫描二维码加入技术交流群

## 📞 联系我们

如果您有任何问题或建议，可以通过以下方式联系我们：

- 📧 邮箱: athena-security@gls.com
- 🐛 Issues: [GitHub Issues](https://github.com/gls/athena-security/issues)
- 💬 讨论: [GitHub Discussions](https://github.com/gls/athena-security/discussions)

感谢您对 Athena Security 项目的贡献！🎉
