# YatCPU

YatCPU (Yet another toy CPU，逸芯) 是一款开源、开发中的教学用 RISC-V 处理器，基于 Chisel 硬件设计语言实现，并用于中山大学 (Sun Yat-sen University) 计算机学院冯班组成原理实验课程的教学。同样欢迎其他高校相关课程使用！



# Docker 配置方法

## 一、远程服务器（Linux）

### 1.0 连接服务器

连接远程服务器，可以使用 VSCode 或者 Xshell 进行连接。在 VSCode 中编写代码体验更佳，所以这里以 VSCode 举例。

1. 进入 VSCode 后，点击左下角打开远程窗口，选择 SSH，之后会自动下载相关的插件
2. 在顶部窗口配置或输入主机，例如 ssh root@111.222.333.44，之后等待 VSCode 配置与连接即可

### 1.1 下载Docker

```bash
# 更新 apt 包索引并安装必要工具包
sudo apt-get update
sudo apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release

# 创建 keyrings 目录并添加 Docker GPG 密钥
sudo mkdir -p /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# 配置 Docker 仓库源
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
      
# 更新 apt 包索引并安装 Docker
sudo apt-get update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

//自动启动配置
# 配置加载
sudo systemctl daemon-reload
# 启动服务
sudo systemctl start docker
# 开启启动
sudo systemctl enable docker
# 查看服务状态
sudo systemctl status docker
```

### 1.2 拉取代码仓库

```bash
git clone https://github.com/howardlau1999/yatcpu //成品
git clone https://github.com/Tokisakix/2023-fall-yatcpu-repo //半成品，用于实验
```

### 1.3 运行镜像，生成容器

```bash
//首次运行，本地不存在镜像，会自动拉取镜像
//运行成品yatcpu容器
docker run -it --rm howardlau1999/yatcpu
```

1. 拉取完镜像之后，安装 Dev Containers 插件
2. 点击左下角打开远程窗口，选择在容器中打开文件夹，选择刚刚拉取的代码仓库即可

### 1.4 sbt测试

```bash
//假设测试 lab1 的代码
cd 2023-fall-yatcpu-repo/lab1 //注意，必须在 build.sbt 存在的目录下进行测试
sbt testOnly InstructionFetch.scala //假设只测试取指的代码
sbt test //全体测试
```



## 二、本地计算机（WSL）

### 2.1 下载Docker

官网链接：https://www.docker.com

下载后启动 Docker，会引导你开启计算机虚拟化（WSL，Windows Subsystem for Linux），要重启生效。并且会并要求你安装适当的 Linux 发行版（如 Ubuntu）来运行 Docker。

### 2.2 拉取代码仓库

```bash
git clone https://github.com/howardlau1999/yatcpu //成品
git clone https://github.com/Tokisakix/2023-fall-yatcpu-repo //半成品，用于实验
```

### 2.3 运行镜像，生成容器

1. 进入 VSCode 后，安装 Dev Containers 插件
2. 点击左下角打开远程窗口，选择在容器中打开文件夹（这时 Docker 必须是启动的），选择刚刚拉取的代码仓库即可

### 2.4 sbt测试

```bash
//假设测试 lab1 的代码
cd 2023-fall-yatcpu-repo/lab1 //注意，必须在 build.sbt 存在的目录下进行测试
sbt testOnly InstructionFetch.scala //假设只测试取指的代码
sbt test //全体测试
```