# 酷云课堂（Classroom）

在线教学系统：课程分类 / 课程管理、前台首页 / 列表 / 详情、视频点播 + 学习记录、问答、角色权限、回收站。

## 技术栈

| 层次 | 选型 |
|---|---|
| 语言 | Java 25 |
| 后端框架 | Spring MVC（Spring Framework 7.0.8，传统 war 工程） |
| 持久层 | MyBatis 3.5.19 + mybatis-spring 4.0.0 |
| 数据库 | MySQL（classroom 库，本机 9.6；连接池 HikariCP） |
| 容器 | Tomcat 11（Jakarta EE 11，代码使用 `jakarta.*`） |
| 构建 | Maven（maven-compiler-plugin 3.14.0，release 25） |
| 前端 | Vue 3 + Vite + Element Plus + Vue Router + Axios |

> 说明：Vue 3 与 Element UI（Vue 2 组件库）不兼容，已按确认改为 **Element Plus**。

## 目录结构

```
Classroom/
├── pom.xml                     # Maven 工程（war）
├── src/main/java/com/classroom/
│   ├── config/                 # WebAppInitializer / RootConfig / WebConfig（JavaConfig，无 web.xml）
│   ├── common/                 # Result / ResultCode / BusinessException / PageResult / 全局异常
│   ├── entity/                 # 15 个实体（与 classroom 库表一一对应，含 messages）
│   ├── mapper/                 # 17 个 MyBatis Mapper 接口
│   ├── service/ + impl/        # 业务层
│   ├── controller/             # 接口层（/api/**）
│   ├── interceptor/            # LoginInterceptor（登录拦截）
│   ├── vo/                     # 视图对象
│   └── util/                   # MD5Util
├── src/main/resources/
│   ├── jdbc.properties         # 数据库连接（localhost:3306 / root / 12345678）
│   └── mapper/*.xml            # MyBatis 映射文件
└── frontend/                   # Vue3 前端工程
    └── src/views/              # Login / Register / Home（后续模块在此扩展）
```

## 已落地模块（按顺序推进）

- [x] **用户登录注册**：注册（默认分配"学员"角色，密码 MD5 存储）、登录（邮箱/手机号 + 密码）、退出、当前用户、登录拦截
- [x] **课程分类 / 课程管理**：后台分类管理（列表/搜索/添加/编辑/伪删除）、课程管理（列表/搜索/添加/编辑/推荐/伪删除，联动删除章节）、章节管理（列表/添加/编辑/伪删除）；前端 `/admin` 后台布局与分类/课程/章节管理页面
- [x] **前台首页 / 列表 / 详情**：首页（Banner 轮播 + 推荐/最新/最热课程，匿名可访问）、课程列表（分类筛选/模糊搜索/最新最热排序/分页）、课程详情（信息 + 章节目录 + 收藏/取消收藏，登录后可收藏）
- [x] **视频点播 + 学习记录**：章节播放页（HTML5 视频播放，支持快进/回退、章节切换）、学习记录（开始学习记录 StartTime、结束记录 EndTime，页面卸载 sendBeacon 兜底）、我的课程学习进度（课程/已学章节数/完成率）
- [x] **问答 + 消息**：点播页本章提问列表与学员提问、我的提问页、后台提问管理（教师/管理员回复，待回答/已回答状态）；消息功能——后台消息管理（管理员/教师发送通知：全体学员或指定学员）、学员端消息列表（未读数角标、定向未读自动已读、消息详情抽屉）
- [x] **角色权限**：登录会话携带角色 ID；后台接口权限拦截（/api/admin/** 仅管理员/老师，角色/功能/用户角色分配仅管理员，学员 403）；后台菜单按角色动态返回（nodes 表：管理员 12 节点、老师 7 节点）；前端后台入口与路由按角色控制；后台用户管理（分配角色，不能改自己）、角色权限管理（角色-功能节点勾选保存）、学习记录查看页
- [x] **回收站**：分类/课程/章节/提问四个回收站 Tab，支持恢复（伪删除数据回到正常列表，恢复课程联动恢复其章节）与彻底删除（物理删除并清理关联数据：学习记录、提问、收藏按外键顺序删除，避免外键冲突）
- [x] **补充：个人中心 + 数据统计**：个人中心 `/profile`（我的课程学习进度、我的收藏列表、我的提问、个人信息与修改密码）；后台统计首页 `/admin/index`（用户/课程/分类/学习记录/提问/消息六项指标、热门课程 TOP5、近 7 天学习记录趋势条形图、各分类课程分布）
- [x] **补充：结业证书 + 继续学习 + 全局搜索**：学完课程（进度 100%）可在个人中心领取结业证书 `/certificate/:cuid`（打印/另存 PDF）；个人中心"继续学习"跳转最近学习章节（进度接口新增 lastChapterId/lastChapterName/finishTime）；顶部导航全局搜索框（跳课程列表并联动关键词）
- [x] **补充：播放器增强 + 构建优化**：倍速播放（0.5–2x）、自动连播下一章、播放位置记忆续播（位置存学习记录 remark 字段，`POST /api/learn/position`，播放页 `resumePosition` 续播，节流 10s 保存 + 离开页 sendBeacon 兜底）；Vite 分包（element-plus / vue-vendor / vendor 独立 chunk，主包 1060KB → 7.5KB）
- [x] **补充：课程评价 + 讲师视频上传 + Element Plus 按需引入**：新增 `course_ratings` 评价表（幂等建表，未动现有数据），课程详情页五星评分+评论（每人每课一条可修改，详情接口返回平均分/评价数/评价列表/我的评分）；讲师后台上传视频（章节管理）/图片（课程封面），`POST /api/admin/upload/{video,image}` 存本地 uploads 目录（jdbc.properties 配置 upload.path，/upload/** 静态映射，上限 200MB）；Element Plus 按需引入（41 组件子路径映射 + 命令式组件样式兜底，element-plus JS 815KB→354KB、CSS 361KB→215KB）
- [x] **章节目录三部分（视频 + 文件 + 章节小测）**：新增 `chapter_files`（章节课件）、`chapter_quizzes`（单选题目）、`user_quiz_results`（学员作答记录，UID+QID 唯一可重答覆盖）三张表（幂等建表，未动现有数据与表）；课程详情章节目录每章显示「视频 / 文件 / 小测」三入口，文件弹窗可查看下载，小测进入答题页（提交后判分、显示正误与正确答案、可重新作答）；管理员与教师端在章节管理均可设置三部分：上传章节课件（pdf/word/ppt/excel/txt/zip 等，`POST /api/admin/upload/file`）绑定章节、增删改小测题目（选项 A-D + 答案）、视频沿用原有上传；前台文件列表接口 `/api/chapter-files/{chid}` 匿名可访问，小测接口 `/api/quiz/**` 需登录
- [x] **Web 全流程测试（2026-09-28）**：环境（8080/5173/MySQL 连通）→ 后端接口全量冒烟 **73/73 通过**（匿名公开接口 + 学员/教师/管理员三角色业务接口 + 权限边界 401/403 + 404 兜底 + 用户管理写操作往返恢复）→ 浏览器实测（登录页渲染与红色校验提示、学员登录首页、课程详情章节目录三入口、管理员后台用户管理、禁用/启用闭环、分配角色弹窗）。测试中修复 2 个前端问题：① `UserManage.vue` 模板直接访问全局 `sessionStorage` 导致表格渲染崩溃（改为 script 内 `myUid` 常量）；② Element Plus 按需引入未注册 `v-loading` 指令导致 loading 遮罩不生效（`main.js` 注册 `vLoading`）

## 启动步骤

### 1. 后端（IDEA）

1. 用 IDEA 打开本目录 `Classroom`，等待 Maven 导入依赖；
2. 确保 IDEA 的 Project SDK 为 **JDK 25**，Maven 使用 3.9+；
3. 配置 **Tomcat 11** 运行（Deployment 选择 war exploded，Application context `/`）；
4. 确认 `src/main/resources/jdbc.properties` 连接信息（默认 root / 12345678）；
5. 启动 Tomcat，后端地址 `http://localhost:8080`。

### 2. 前端

```bash
cd frontend
npm install
npm run dev        # http://localhost:5173，/api 自动代理到 8080
```

### 3. 现有测试账号

| 账号 | 密码 |
|---|---|
| admin（管理员） | 123456 |
| teacher（老师） | 123456 |
| student / stu03（学员） | 123456 |

> 现有密码均为 MD5 存储（`e10adc3949ba59abbe56e057f20f883e` = MD5("123456")）。

## 数据库

- 库名：`classroom`（14 张业务表 + messages，保留现有数据）；
- 建表脚本：项目根目录上一级的 `Classroom.sql` 可按需重建（新三表 chapter_files / chapter_quizzes / user_quiz_results 已在库中建成）；
- 实体类按现有库实际结构生成（`users.Avatar`、`categories.ParentID` 默认 0、`userlearns.EndTime` 可空等）。

## 接口约定

- 统一响应：`{ "code": 200, "message": "...", "data": ... }`；
- 登录态：Session（Cookie 同源传递，前端开发时经 Vite 代理）；
- 未登录访问受保护接口返回 401。
