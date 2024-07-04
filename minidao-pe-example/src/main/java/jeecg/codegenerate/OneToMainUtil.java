//package jeecg.codegenerate;
//
//
//import org.jeecgframework.p3.cg.factory.CodeGenerateFactoryOneToMany;
//import org.jeecgframework.p3.cg.pojo.onetomany.CodeParamEntity;
//import org.jeecgframework.p3.cg.pojo.onetomany.SubTableEntity;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 代码生成器【一对多】
// *
// * @author scott
// * @site www.jeecg.com
// *
// */
//public class OneToMainUtil {
//
//	/**
//	 * 一对多(父子表)数据模型，生成方法
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		//第一步：设置主表配置
//		CodeParamEntity codeParamEntityIn = new CodeParamEntity();
//
//		codeParamEntityIn.setTableName("jeecg_order_main");//主表名
//		codeParamEntityIn.setEntityName("Order");	 //实体名
//		codeParamEntityIn.setFtlDescription("订单");	 //描述
//
//		//第二步：设置子表集合配置
//		List<SubTableEntity> subTabParamIn = new ArrayList<SubTableEntity>();
//		//[1].子表一
//		SubTableEntity po = new SubTableEntity();
//		po.setTableName("jeecg_order_customer");//子表名
//		po.setEntityName("Custom");	    //实体名
//		po.setEntityPackage("test");	        //包名
//		po.setFtlDescription("客户明细");       //描述
//		//子表外键参数配置
//		po.setForeignKey("order_id");//子表外键
//		po.setMainForeignKey("id");//对应的主表字段
//		subTabParamIn.add(po);
//		//[2].子表二
//		SubTableEntity po2 = new SubTableEntity();
//		po2.setTableName("jeecg_order_ticket");		//子表名
//		po2.setEntityName("Product");			//实体名
//		po2.setEntityPackage("test"); 				//包名
//		po2.setFtlDescription("机票明细");			//描述
//		//子表外键参数配置
//		po2.setForeignKey("order_id");//子表外键
//		po2.setMainForeignKey("id");//对应的主表字段
//		subTabParamIn.add(po2);
//		codeParamEntityIn.setSubTabParam(subTabParamIn);
//
//		//第三步：一对多(父子表)数据模型,代码生成
//		CodeGenerateFactoryOneToMany.oneToManyCreate(codeParamEntityIn,subTabParamIn);
//	}
//}
