// Copyright 2021 Howard Lau
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package riscv.core.fivestage_final

import chisel3._
import riscv.Parameters

class Control extends Module {
  val io = IO(new Bundle {
    val jump_flag = Input(Bool())
    val jump_instruction_id = Input(Bool())
    val rs1_id = Input(UInt(Parameters.PhysicalRegisterAddrWidth))
    val rs2_id = Input(UInt(Parameters.PhysicalRegisterAddrWidth))
    val memory_read_enable_ex = Input(Bool())
    val rd_ex = Input(UInt(Parameters.PhysicalRegisterAddrWidth))
    val memory_read_enable_mem = Input(Bool())
    val rd_mem = Input(UInt(Parameters.PhysicalRegisterAddrWidth))

    val if_flush = Output(Bool())
    val id_flush = Output(Bool())
    val pc_stall = Output(Bool())
    val if_stall = Output(Bool())
  })

  // Lab3(Final)
  //若当前译码的指令（非跳转）依赖的操作数的来源为普通指令，无需阻塞，可以无缝转发
  //若当前译码的指令（非跳转）依赖的操作数的来源为load指令，需要阻塞到MEM阶段转发读取
  //若当前译码的指令为跳转指令，因为要在ID阶段提前计算跳转地址，分为两种情况：
  //  1.依赖的操作数的来源为普通指令，需要阻塞到EX阶段转发读取
  //  2.依赖的操作数的来源为load指令，需要阻塞到MEM阶段转发读取
  val id_hazard = ((io.memory_read_enable_ex || io.jump_instruction_id) && io.rd_ex =/= 0.U && (io.rs1_id === io.rd_ex || io.rs2_id === io.rd_ex)) ||
                  ((io.memory_read_enable_mem && io.jump_instruction_id) && io.rd_mem =/= 0.U && (io.rs1_id === io.rd_mem || io.rs2_id === io.rd_mem))

  when(id_hazard) {
    io.if_flush := false.B
    io.id_flush := true.B
    io.pc_stall := true.B
    io.if_stall := true.B
  }.elsewhen(io.jump_flag) {
    io.if_flush := true.B
    io.id_flush := false.B
    io.pc_stall := false.B
    io.if_stall := false.B
  }.otherwise {
    io.if_flush := false.B
    io.id_flush := false.B
    io.pc_stall := false.B
    io.if_stall := false.B
  }

  // io.if_flush := io.jump_flag && !id_hazard
  // io.id_flush := id_hazard
  // io.pc_stall := id_hazard
  // io.if_stall := id_hazard

  // Lab3(Final) End
}
