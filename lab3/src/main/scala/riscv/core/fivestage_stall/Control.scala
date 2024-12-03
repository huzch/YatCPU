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

package riscv.core.fivestage_stall

import chisel3._
import riscv.Parameters

class Control extends Module {
  val io = IO(new Bundle {
    val jump_flag = Input(Bool())
    val rs1_id = Input(UInt(Parameters.PhysicalRegisterAddrWidth))
    val rs2_id = Input(UInt(Parameters.PhysicalRegisterAddrWidth))
    val rd_ex = Input(UInt(Parameters.PhysicalRegisterAddrWidth))
    val reg_write_enable_ex = Input(Bool())
    val rd_mem = Input(UInt(Parameters.PhysicalRegisterAddrWidth))
    val reg_write_enable_mem = Input(Bool())

    val if_flush = Output(Bool())
    val id_flush = Output(Bool())
    val pc_stall = Output(Bool())
    val if_stall = Output(Bool())
  })

  // Lab3(Stall)
  //若当前译码的指令依赖的操作数的来源为所有指令（无论是普通指令还是load指令），都需要阻塞到WB阶段读取
  val id_hazard = (io.reg_write_enable_ex && io.rd_ex =/= 0.U && (io.rs1_id === io.rd_ex || io.rs2_id === io.rd_ex)) ||
                  (io.reg_write_enable_mem && io.rd_mem =/= 0.U && (io.rs1_id === io.rd_mem || io.rs2_id === io.rd_mem))

  when(io.jump_flag) {
    io.if_flush := true.B
    io.id_flush := true.B
    io.pc_stall := false.B
    io.if_stall := false.B
  }.elsewhen(id_hazard) {
    io.if_flush := false.B
    io.id_flush := true.B
    io.pc_stall := true.B
    io.if_stall := true.B
  }.otherwise {
    io.if_flush := false.B
    io.id_flush := false.B
    io.pc_stall := false.B
    io.if_stall := false.B
  }

  // io.if_flush := io.jump_flag
  // io.id_flush := io.jump_flag || id_hazard
  // io.pc_stall := id_hazard && !io.jump_flag
  // io.if_stall := id_hazard && !io.jump_flag
  
  // Lab3(Stall) End
}
