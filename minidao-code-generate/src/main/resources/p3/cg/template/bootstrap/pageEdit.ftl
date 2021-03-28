<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width" />
<title>Jeecg-P3插件开发平台</title>
<!-- Jquery组件引用 -->
<script src="$!{basePath}/plug-in-ui/js/jquery-1.9.1.js"></script>
<!-- bootstrap组件引用 -->
<link href="$!{basePath}/plug-in-ui/css/bootstrap.min.css" rel="stylesheet">
<script src="$!{basePath}/plug-in-ui/hplus/js/bootstrap.min.js"></script>

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

<#assign listSize=0 />
<#list columnDatas as item>
		<#if item.domainPropertyName != 'id'>
					 <#if item.columnName?lower_case != 'del_stat' 
										    && item.columnName?lower_case != 'create_by'
											&& item.columnName?lower_case != 'create_name' 
											&& item.columnName?lower_case != 'create_date' 
											&& item.columnName?lower_case != 'update_by'
											&& item.columnName?lower_case != 'update_name' 
											&& item.columnName?lower_case != 'update_date' 
											&& item.columnName?lower_case != 'creator'
											&& item.columnName?lower_case != 'editor' 
											&& item.columnName?lower_case != 'create_dt' 
											&& item.columnName?lower_case != 'edit_dt' 
											&& item.columnName?lower_case != 'last_edit_dt' 
											&& item.columnName?lower_case != 'record_version'>
							  <#assign listSize=listSize+1 />
						</#if>
											
		</#if>
</#list>
</head>
<body style="margin: 20px">
	<form id="formobj" action="$!{basePath}/${projectName}/${lowerName}.do?doEdit" class="form-horizontal validform" role="form"  method="post">
		<input type="hidden" id="btn_sub" class="btn_sub"/>
	    <input id="id" name="id" type="hidden" value="$!{${lowerName}.id}">
	    <#list columnDatas as item>
		      <#if item.domainPropertyName != 'id'>
		         <#if item.columnName?lower_case != 'del_stat' 
										    && item.columnName?lower_case != 'create_by'
											&& item.columnName?lower_case != 'create_name' 
											&& item.columnName?lower_case != 'create_date' 
											&& item.columnName?lower_case != 'update_by'
											&& item.columnName?lower_case != 'update_name' 
											&& item.columnName?lower_case != 'update_date' 
											&& item.columnName?lower_case != 'creator'
											&& item.columnName?lower_case != 'editor' 
											&& item.columnName?lower_case != 'create_dt' 
											&& item.columnName?lower_case != 'edit_dt' 
											&& item.columnName?lower_case != 'last_edit_dt' 
											&& item.columnName?lower_case != 'record_version'>
		                  <#if item.columnType == "datetime" ||item.columnType == "date" || item.columnType == "timestamp">
			                   <#if listSize lte 10>	
								    <div class="form-group">
							   <#else>
								    <div class="col-xs-12 col-sm-12 col-md-6" style="margin-bottom: 10px;">
							   </#if>
										<label class="col-sm-3 control-label">${item.columnComment}</label>
										<div class="col-sm-7">
											<div class="input-group" style="width:100%">
							                    <input type="text" name="${item.domainPropertyName}" id="${item.domainPropertyName}" class="form-control input-sm" value="$!{${lowerName}.${item.domainPropertyName}}"   <#if item.nullable != 'Y'> datatype="*" </#if> placeholder="请输入${item.columnComment}" />
							                    <span class="input-group-addon" >
							                        <span class="glyphicon glyphicon-calendar"></span>
							                    </span>
							                </div>
										</div>
									</div>
                            <#else>
			                   <#if listSize lte 10>	
								 <div class="form-group">
							   <#else>
								 <div class="col-xs-12 col-sm-12 col-md-6" style="margin-bottom: 10px;">
							   </#if>
									<label for="name" class="col-sm-3 control-label">${item.columnComment}</label>
									<div class="col-sm-7">
										<div class="input-group" style="width:100%">
											<input type="text" class="form-control input-sm" id="${item.domainPropertyName}"  name="${item.domainPropertyName}" value="$!{${lowerName}.${item.domainPropertyName}}" <#if item.nullable != 'Y'> datatype="*"</#if> placeholder="请输入${item.columnComment}" />
										</div>
									</div>
								</div>
				           </#if>
				    </#if>
			   </#if>
			</#list> 
	  </form>	
</body>
</html>
<script type="text/javascript">
	$(document).ready(function() {
		 <#list columnDatas as item>
		<#if item.columnType == "datetime" ||item.columnType == "date" || item.columnType == "timestamp">
		//日期控件初始化
	    laydate.render({
		   elem: '#${item.domainPropertyName}'
		  ,type: '${item.columnType}'
		  ,trigger: 'click' //采用click弹出
		  ,ready: function(date){
		  	 $("#${item.domainPropertyName}").val(DateJsonFormat(date,this.format));
		  }
		});
		</#if>
		</#list>
		 //自定义checkbox样式
        $('.i-checks').iCheck({
            checkboxClass: 'icheckbox_square-green',
            radioClass: 'iradio_square-green'
        });
		
		//表单提交
		$("#formobj").Validform({
			tiptype:function(msg,o,cssctl){
				if(o.type==3){
					validationMessage(o.obj,msg);
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
							obj.parent().next().find(
									".Validform_checktip")
									.show();
							obj.find(".passwordStrength")
									.hide();
						} else {
							$(".passwordStrength").show();
							obj.parent().next().find(
									".Validform_checktip")
									.hide();
						}
					}
				}
			},
			callback : function(data) {
				if (data.success == true) {
					var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
					var win = window.parent;
					parent.layer.close(index); 
					win.tip(data.msg,'1');
				} else {
				    var win = window.parent;
					win.tip(data.msg,'2');
				}
			}
		});
	});
</script>