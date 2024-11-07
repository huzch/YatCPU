module Top(
  input   clock,
  input   reset,
  output  io_tx, // @[src/main/scala/board/z710/Top.scala 25:14]
  input   io_rx, // @[src/main/scala/board/z710/Top.scala 25:14]
  output  io_led // @[src/main/scala/board/z710/Top.scala 25:14]
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] led_count; // @[src/main/scala/board/z710/Top.scala 95:26]
  wire [31:0] _led_count_T_1 = led_count + 32'h1; // @[src/main/scala/board/z710/Top.scala 99:28]
  assign io_tx = 1'h0; // @[src/main/scala/board/z710/Top.scala 45:9]
  assign io_led = led_count >= 32'h1312d00; // @[src/main/scala/board/z710/Top.scala 101:24]
  always @(posedge clock) begin
    if (reset) begin // @[src/main/scala/board/z710/Top.scala 95:26]
      led_count <= 32'h0; // @[src/main/scala/board/z710/Top.scala 95:26]
    end else if (led_count >= 32'h2625a00) begin // @[src/main/scala/board/z710/Top.scala 96:34]
      led_count <= 32'h0; // @[src/main/scala/board/z710/Top.scala 97:15]
    end else begin
      led_count <= _led_count_T_1; // @[src/main/scala/board/z710/Top.scala 99:15]
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  led_count = _RAND_0[31:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
