import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage
object ALUOp {
    val ADD = 0.U(1.W)
    val MUL = 1.U(1.W)
}


class ALU(dataWidth: Int) extends Module {
  val io = IO(new Bundle {
    val operand_a = Input(UInt(dataWidth.W))
    val operand_b = Input(UInt(dataWidth.W))
    val alu_op = Input(UInt(1.W)) // 2 bits to encode ADD and MUL
    val valid = Input(Bool())
    val add_result = Output(UInt(dataWidth.W))
    val mul_result = Output(UInt((2*dataWidth).W))
    val add_result_valid = Output(Bool())
    val mul_result_valid = Output(Bool())
  })
  import ALUOp._


  // Registers
  val mul_reg_a = Reg(UInt(dataWidth.W))
  val mul_reg_b = Reg(UInt(dataWidth.W))
  val mul_result_reg = Reg(UInt((2*dataWidth).W)) // Store the wider result of multiplication
  val mul_valid_reg = RegInit(false.B)
  val mul_result =Reg(UInt((2*dataWidth).W))
  val add_result = Reg(UInt(dataWidth.W))

  io.add_result_valid := false.B
  io.mul_result_valid := false.B
  io.add_result := 0.U
  io.mul_result := 0.U  
  when(io.valid && io.alu_op === MUL ) {
    mul_reg_a := io.operand_a
    mul_reg_b := io.operand_b
    mul_valid_reg := true.B
  }.elsewhen(mul_valid_reg ){ // Multiplication in progress
    mul_valid_reg := false.B //reset multiplication
    io.mul_result := mul_reg_a * mul_reg_b //Truncate the result to desired width
    io.mul_result_valid := true.B
  }.elsewhen(io.valid && io.alu_op === ADD) {
    io.add_result := io.operand_a + io.operand_b
    io.add_result_valid := true.B
  }
}


object ALU extends App {
  println(chisel3.getVerilogString(new ALU(32))) 
  ChiselStage.emitSystemVerilogFile(
    new ALU(32)
  )
  // save the output to a file
}
