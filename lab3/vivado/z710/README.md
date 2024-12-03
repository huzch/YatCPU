
# 如何使用脚本使 CPU 烧录至 Zybo Z7-10 开发板并读取 UART 输出至电脑

## 0. 预先准备

在执行下述步骤前，请确保完成：

1. 通过 CPUTest 确保实现无误
2. 在 `src\main\scala\z710\Top.scala` 运行 `VerilogGenerator` 并生成 verilog\z710\Top.v 。
3. 安装 board files 至 vivado。在 https://github.com/Digilent/vivado-boards 下载压缩包，然后将压缩包中 new\board\_files 目录下以 `zybo` 开头的三个文件夹复制到如 E:\Xilinx\Vivado\2020.1\data\boards\board\_files 目录下。


此处用 `vivado` 和 `xsct` 工具，并假设您已在 `vivado/z710` 目录下打开终端。若您未将 `vivado.bat` 和 `xsct` 添加到系统 PATH，则应将其替换为如 E:\Xilinx\Vivado\2020.1\bin\vivado 和  E:\Xilinx\Vitis\2020.1\bin\xsct  。

若您使用 2020.1 或较久版本的 Xilinx 工具，在 Windows Powershell 中执行将在额外弹出的窗口中运行 `vivado` 和 `xsct`。您可以在命令前添加 `cmd /c` 以指定其在 cmd 中运行并保留输出至本窗口。

## 1. 生成 Vivado 项目

确保您的 `verilog/z710` 下已生成 Top.v ，且其他组件的 verilog 文件都存在。
执行命令以生成设计：

```pwsh
cmd /c vivado -mode batch -source ./riscv-z710-v2020.tcl
```

您应见文件夹 `vivado/z710/riscv-z710-v2020` 被创建，且其中有 `riscv-z710-v2020.xpr`。请您注意运行过程中任何错误提示，包括未安装 board files 提示 parts 未找到、未找到对应文件等情况。



## 2. 生成比特流并导出硬件平台

完成上一步骤后，执行命令以生成比特流、导出硬件平台 .xsa 文件。

```pwsh
cmd /c vivado -mode batch -source ./generate_bitstream.tcl
```

完成后，您应见 `vivado\z710\riscv-z710-v2020\design_1_wrapper.xsa` 文件。


## 3. 构建 Vitis 项目并烧录至开发板

完成上一步骤后，将开发板连接至电脑，并开启电源。随后执行命令将 CPU 烧录至开发板，并使 Zynq 处理器监听并返回 UART 输出：

```pwsh
cmd /c xsct ./vitis_prj_run.tcl
```

若成功，您应看到开发板的 LD0 灯在打开其下方的 G15 开关（该开关控制输入CPU的时钟开关）时会闪烁，同时右侧 BTN16 上方的 LD12 已烧录指示灯亮起为绿色。

打开任意的 COM 通信工具，连接至合适串口，即可收到 CPU 的输出 UART 信息，按 BTN0 （K18）会重置 CPU 以重新打印。



## For Maintainers

该段描述这些脚本如何获得，若您希望了解其中原理或做出贡献，可以阅读本节。

### lab3 auxiliary file related

CPU 输入频率4分频。

### Use with Vivado 2020.1

#### Vivado 项目重建脚本

2020 版本的 Vivado 导出仍会引用 .bd 块设计文件（2022不会），即使勾选了用 TCL 重建块设计。从 Vivado 2022 版那里导出的脚本薅来一段代码让它不必引用 .bd 文件，从而单个文件就能重建项目。

用 Vivado 2020 的 Write Project Tcl 导出脚本后，开头会提示需要的文件，包括了块设计的 `design_1_wrapper.v`，但里面其实已经有 TCL 连线。

1. 在 `Set 'sources_1' fileset object` （Line 128）附近删除添加 `design_1_wrapper.v` 的一行
2. 在注释`End of cr_bd_design_1()` 后，运行完 `cr_bd_design_ ""` 之后（Line 870），添加生成 wrapper Verilog 文件的指令，如下
```tcl
...
# End of cr_bd_design_1()
cr_bd_design_1 ""
set_property REGISTERED_WITH_MANAGER "1" [get_files design_1.bd ] 
set_property SYNTH_CHECKPOINT_MODE "Hierarchical" [get_files design_1.bd ] 

# 加在这： call make_wrapper to create wrapper files
set wrapper_path [make_wrapper -fileset sources_1 -files [ get_files -norecurse design_1.bd] -top]
add_files -norecurse -fileset sources_1 $wrapper_path
```

3. 修正脚本中使用了导出电脑上绝对路径的命令，在 `Adding sources referenced in BDs, if not already added`（Line 191）处使用的绝对路径替换为项目文件夹下相对路径。

```tcl
# Adding sources referenced in BDs, if not already added
if { [get_files Top.v] == "" } {
  import_files -quiet -fileset sources_1 "${origin_dir}/../../verilog/z710/Top.v"
}
if { [get_files clock_control.v] == "" } {
  import_files -quiet -fileset sources_1 "${origin_dir}/../../verilog/z710/clock_control.v"
}
```

修改后上述脚本就能重建项目。

`open_project.tcl` 直接打开项目的 .xpr 项目文件，`generate_bitstream.tcl` 再生成比特流，并额外导出硬件平台。

#### Vitis 项目重建脚本

Vitis 仅在控制台的 Platform 控制台打印一些对硬件平台的操作指令，其他指令需要参照 XSCT（Xilinx Software Commandline Tool） 参考手册，以使用相关指令（纯苦工活）。一些流程在 Vitis IDE 里面的设置可以找到，比如 PS7 处理器的初始化流程（在脚本注释有写）。

XSCT 会单独使用一个 cmd 窗口执行，因此如果中途因错误退出将来不及看到任何错误信息，因此需要在命令行打开 xsct（如 `E:\Xilinx\Vitis\2020.1\bin\xsct`），然后逐行执行（多行的 if 结构可一次性复制多行），并查看错误。

注意 Vivado 和 Vitis 版本要一致，不要混用不同年份的版本。

***

### Use with Vivado 2022.1

#### Vivado 项目重建脚本

导（出）就完啦！

#### Vitis 项目重建脚本

应该和 2020 的一样。