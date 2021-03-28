#set($temp = "$")
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width" />
<title>${codeName}</title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width, initial-scale=1">
<#assign uploadFlag=0>
<#assign hasDate = 0>
<#assign hasDateTime = 0>
<#list columnDatas as po>
<#if uploadFlag==0 && (po.columnType=='file' || po.columnType == 'image')>
<#assign uploadFlag=1>
</#if>
<#if hasDate==0 && po.columnType=='date'>
<#assign hasDate=1>
</#if>
<#if hasDateTime==0 && po.columnType=='date'>
<#assign hasDateTime=1>
</#if>
</#list>
<!-- Jquery组件引用 -->
<script src="$!{basePath}/plug-in-ui/js/jquery-1.9.1.js"></script>

<!-- bootstrap组件引用 -->
<link href="$!{basePath}/plug-in-ui/css/bootstrap.min.css" rel="stylesheet">
<script src="$!{basePath}/plug-in-ui/hplus/js/bootstrap.min.js"></script>

<!-- bootstrap table组件以及中文包的引用-->
<link href="$!{basePath}/plug-in-ui/css/bootstrap-user-defined/bootstrap-table.min.css" rel="stylesheet">
<script src="$!{basePath}/plug-in-ui/hplus/js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script src="$!{basePath}/plug-in-ui/hplus/js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>

<!-- icheck组件引用 -->
<link href="$!{basePath}/plug-in-ui/hplus/css/plugins/iCheck/custom.css" rel="stylesheet">
<script type="text/javascript" src="$!{basePath}/plug-in-ui/hplus/js/plugins/iCheck/icheck.min.js"></script> 

<!-- Validform组件引用 -->
<script type="text/javascript" src="$!{basePath}/plug-in-ui/js/Validform_v5.3.2_min.js"></script> 
<link href="$!{basePath}/plug-in-ui/js/validform/validform-ext.css" rel="stylesheet">
<script type="text/javascript" src="$!{basePath}/plug-in-ui/plugin/passwordStrength/passwordStrength-min.js"></script> 

<!-- Layer组件引用 -->
<script src="$!{basePath}/plug-in-ui/layer/layer.js"></script>
<script src="$!{basePath}/plug-in-ui/layer/laydate/laydate.js"></script>
<link href="$!{basePath}/plug-in-ui/layer/laydate/theme/default/laydate.css" rel="stylesheet">

<!-- 通用组件引用 -->
<script src="$!{basePath}/plug-in-ui/js/bootstrap-user-defined/common.js"></script>
<script src="$!{basePath}/plug-in-ui/js/My97DatePicker/WdatePicker.js"></script>
</head>
<body>
<div class="container" style="width:100%;overflow-x:hidden">
<div class="panel panel-default">
<div class="panel-heading"></div>
<div class="panel-body">
<form class="form-horizontal" role="form" id="formobj" action="$!{basePath}/${projectName}/${lowerName}.do?doAdd" method="POST">
	<input type="hidden" id="btn_sub" class="btn_sub"/>
	<input type="hidden" id="id" name="id"/>
	<fieldset>
		<legend>${codeName}</legend>
		<div class="main-form">
			<div class="row">
			<#list columnDatas as po>
				<#if po.domainPropertyName != 'id'>
				<div class="bt-item col-md-6 col-sm-6">
					<div class="row">
						<div class="col-md-3 col-sm-3 col-xs-3 bt-label">
							${po.columnComment}：
						</div>
						<div class="col-md-8 col-sm-8 col-xs-8 bt-content">
							<#if po.columnType == "datetime" || po.columnType == "date" || po.columnType == "timestamp">
							<input type="text" value="$!dateTool.format("yyyy-MM-dd",$!{${lowerName}.${po.domainPropertyName}})" name="${po.domainPropertyName}" id="${po.domainPropertyName}" class="form-control" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd'})"   style="background: url('$!{basePath}/plug-in-ui/images/datetime.png') no-repeat scroll right center transparent;" <#if po.nullable != 'Y'> datatype="*" </#if>/>
							<#else>
							<input type="text" value="$!{${lowerName}.${po.domainPropertyName}}" name="${po.domainPropertyName}" id="${po.domainPropertyName}" class="form-control" <#if po.nullable != 'Y'> datatype="*" </#if>/>
							</#if>
		            	</div>
					</div>
				</div>
				</#if>
			</#list>
			</div>
		</div>
	</fieldset>

	<ul class="nav nav-tabs" style="margin-bottom:0" id="subTabs">
	<#list subEntityList as key>
		<#if key_index==0>
		<li class="active"><a href="#${key.paramData.lowerName}" data-toggle="tab">${key.paramData.codeName}</a></li>  
		<#else>
		<li><a href="#${key.paramData.lowerName}" data-toggle="tab">${key.paramData.codeName}</a></li>  
		</#if>
	</#list>
	</ul>
	<div class="tab-content" style="background-color:#fff;padding-top:10px;">
	<#list subEntityList as key>
		<#if key_index==0>
		<div class="tab-pane fade in active" id="${key.paramData.lowerName}">  
		<#else>
		<div class="tab-pane fade" id="${key.paramData.lowerName}">  
		</#if>
			<div class="form-tb-toolbar">
				<button onclick="addOneRow('${key.paramData.lowerName}_table')" type="button" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-plus"></span>&nbsp;添加</button>
				<button onclick="deleteSelectRows('${key.paramData.lowerName}_table')" type="button" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-minus"></span>&nbsp;删除</button>
			</div>
	   		<div style="overflow-x:auto">
		   		<table class="table subinfo-table" id="${key.paramData.lowerName}_table">
			   		<thead>
			   		<tr>
			   			<th align="center" style="width:25px;"></th>
			   		<#list key.paramData.columnDatas as po>
			   			<#if po.domainPropertyName != 'id'>
						<th>${po.columnComment}</th>
						</#if>
					</#list>
					</thead>
					<tbody>
				  	<tr>
				  		<td class="form-ck"><input type="checkbox" name="ck"/></td>
					  	<#list key.paramData.columnDatas as po>
					  	<#if po.domainPropertyName != 'id'>
						<#if po.columnType == "datetime" || po.columnType == "date" || po.columnType == "timestamp">
						<td>
							<input type="text"  name="${key.paramData.lowerName}Entities[0].${po.domainPropertyName}" id="${key.paramData.lowerName}Entities[0].${po.domainPropertyName}" class="form-control" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd'})"   style="background: url('$!{basePath}/plug-in-ui/images/datetime.png') no-repeat scroll right center transparent;" <#if po.nullable != 'Y'> datatype="*" </#if>/>
						 </td>
						<#else>
						<td>
							<input type="text" name="${key.paramData.lowerName}Entities[0].${po.domainPropertyName}" id="${key.paramData.lowerName}Entities[0].${po.domainPropertyName}" class="form-control" <#if po.nullable != 'Y'> datatype="*" </#if>/>
						 </td>
						</#if>
						</#if>
						</#list>
				  	</tr>
				 	</tbody>
				</table>
			</div>
		</div>
	</#list>
	</div>
</form>
</div>
</div>
</div>
<table style="display:none">
<#list subEntityList as key>
	<tbody id="${key.paramData.lowerName}_table_template">
		<tr>
			<td class="form-ck"><input type="checkbox" name="ck"/></td>
			<#list key.paramData.columnDatas as po>
			<#if po.domainPropertyName != 'id'>
				<#if po.columnType == "datetime" || po.columnType == "date" || po.columnType == "timestamp">
				<td>
					<input type="text"  name="${key.paramData.lowerName}Entities[#index#].${po.domainPropertyName}" id="${key.paramData.lowerName}Entities[#index#].${po.domainPropertyName}" class="form-control" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd'})"   style="background: url('$!{basePath}/plug-in-ui/images/datetime.png') no-repeat scroll right center transparent;" <#if po.nullable != 'Y'> datatype="*" </#if>/>
				 </td>
				<#else>
				<td>
					<input type="text" name="${key.paramData.lowerName}Entities[#index#].${po.domainPropertyName}" id="${key.paramData.lowerName}Entities[#index#].${po.domainPropertyName}" class="form-control" <#if po.nullable != 'Y'> datatype="*" </#if>/>
				 </td>
				</#if>
			</#if>
			</#list>
		</tr>
	</tbody>
</#list>
</table>
<script type="text/javascript">
$(document).ready(function() {
	formControlInit();
	//表单提交
	$("#formobj").Validform({
		tiptype:function(msg,o,cssctl){
			if(o.type==3){
				var oopanel = $(o.obj).closest(".tab-pane");
				var a = 0;
				if(oopanel.length>0){
					var panelID = oopanel.attr("id");
					if(!!panelID){
						var waitActive = $('#subTabs a[href="#'+panelID+'"]');
						if(!waitActive.hasClass("active")){
							waitActive.tab('show')
							a = 1;
						}
					}
				}
				if(a==1){
					setTimeout(function(){validationMessage(o.obj,msg);},366);
				}else{
					validationMessage(o.obj,msg);
				}
			}else{
				removeMessage(o.obj);
			}
		},
		btnSubmit : "#btn_sub",
		btnReset : "#btn_reset",
		ajaxPost : true,
		beforeSubmit : function(curform) {
		},
		usePlugin : {
			passwordstrength : {
				minLen : 6,
				maxLen : 18,
				trigger : function(obj, error) {
					if (error) {
						obj.parent().next().find(".Validform_checktip").show();
						obj.find(".passwordStrength").hide();
					} else {
						$(".passwordStrength").show();
						obj.parent().next().find(".Validform_checktip").hide();
					}
				}
			}
		},
		callback : function(data) {
			if (data.success == true) {
				var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
				parent.layer.alert(data.msg,'1');
				parent.layer.close(index); 
			} else {
				parent.layer.alert(data.msg,'2');
			}
		}
	});
});
function formControlInit(){
<#if hasDateTime == 1>
	$(".laydate-datetime").each(function(){
		if($(this).attr("name").indexOf('#index#')<=0){
			var _this = this;
			laydate.render({
			  elem: this,
			  format: 'yyyy-MM-dd HH:mm:ss',
			  type: 'datetime',
			  done:function(){
				  _this.focus();
			  }
			});
		}
	});
</#if>
<#if hasDate == 1>
	$(".laydate-date").each(function(){
		if($(this).attr("name").indexOf('#index#')<=0){
			var _this = this;
			laydate.render({
			  elem: this,
			  done:function(){
				  _this.focus();
			  }
			});
		}
	});
</#if>
	//单选框/多选框初始化
	$('.i-checks').iCheck({
		labelHover : false,
		cursor : true,
		checkboxClass : 'icheckbox_square-green',
		radioClass : 'iradio_square-green',
		increaseArea : '20%'
	});
}
//初始化下标
function resetTrNum(tableId) {
	$!{temp}tbody = $("#"+tableId+"");
	$!{temp}tbody.find('tbody > tr').each(function(i){
		$(':input, select,button,a', this).each(function(){
			var $!{temp}this = $(this),validtype_str = $!{temp}this.attr('validType'), name = $!{temp}this.attr('name'),id=$!{temp}this.attr('id'),onclick_str=$!{temp}this.attr('onclick'), val = $!{temp}this.val();
			if(name!=null){
				if (name.indexOf("#index#") >= 0){
					$!{temp}this.attr("name",name.replace('#index#',i));
				}else{
					var s = name.indexOf("[");
					var e = name.indexOf("]");
					var new_name = name.substring(s+1,e);
					$!{temp}this.attr("name",name.replace(new_name,i));
				}
			}
			if(id!=null){
				if (id.indexOf("#index#") >= 0){
					$!{temp}this.attr("id",id.replace('#index#',i));
				}else{
					var s = id.indexOf("[");
					var e = id.indexOf("]");
					var new_id = id.substring(s+1,e);
					$!{temp}this.attr("id",id.replace(new_id,i));
				}
			}
			if(onclick_str!=null){
				if (onclick_str.indexOf("#index#") >= 0){
					$!{temp}this.attr("onclick",onclick_str.replace(/#index#/g,i));
				}else{
				    var s = onclick_str.indexOf("[");
					var e = onclick_str.indexOf("]");
					var new_onclick_str = onclick_str.substring(s+1,e);
					$!{temp}this.attr("onclick",onclick_str.replace(new_onclick_str,i));
				}
			}
			if(validtype_str!=null){
				if(validtype_str.indexOf("#index#") >= 0){
					$!{temp}this.attr("validType",validtype_str.replace('#index#',i));
				}
			}
			var class_str = $!{temp}this.attr("class");
			if(!!class_str && class_str.indexOf("i-checks-tpl")>=0){
				$!{temp}this.attr("class",class_str.replace(/i-checks-tpl/,"i-checks"));
			}
		});
	});
}
//新增一行
function addOneRow(tableId){
 	var tr =  $("#"+tableId+"_template tr").clone();
 	 $("#"+tableId).append(tr);
 	 resetTrNum(tableId);
 	 formControlInit();
}
//删除所选行
function deleteSelectRows(tableId){
	<#-- update--begin--author:jiaqiankun date:20180710 for：TASK #2933 【严重bug 代码生成器】一对多table风格，默认编辑数据，点击任意一条明细数据，删除，会把所有的明细删掉 -->
	$("#"+tableId).find("input[name$='ck']:checked").parent().parent().remove(); 
	<#-- update--end--author:jiaqiankun date:20180710 for：TASK #2933 【严重bug 代码生成器】一对多table风格，默认编辑数据，点击任意一条明细数据，删除，会把所有的明细删掉 -->  
    resetTrNum(tableId); 
}
</script>
<#if (cgformConfig.formJs.cgJsStr)?? && cgformConfig.formJs.cgJsStr!="">
<script type="text/javascript">
//JS增强
${cgformConfig.formJs.cgJsStr}
</script>
</#if>
</body>
</html>