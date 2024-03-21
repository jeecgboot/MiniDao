//package jeecg.codegenerate;
//
//import org.jeecgframework.p3.cg.def.FtlDef;
//import org.jeecgframework.p3.cg.factory.CodeGenerateFactory;
//
///**
// * 描述：代码生成器工具类（支持单表）
// * 
// * @author：scott
// * @version:1.0
// */
//public class CodeGenerateUtil {
//
//	public static void main(String[] args) {
//		//表名（多个表可用逗号隔开）
//		String code_cg_tables = "sys_dict";
//		
//		//执行代码生成器
//		for (String tableName : code_cg_tables.split(",")) {
//			CodeGenerateFactory.codeGenerateByFTL(tableName, "项目", FtlDef.KEY_TYPE_02);
//		}
//	}
//}
